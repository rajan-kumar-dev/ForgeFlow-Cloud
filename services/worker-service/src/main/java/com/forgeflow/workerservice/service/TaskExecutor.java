
package com.forgeflow.workerservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.forgeflow.workerservice.api.JobApiClient.ExecutionJob;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Component("forgeFlowTaskExecutor")
public class TaskExecutor {

    private static final Set<String> ALLOWED_FEED_HOSTS = Set.of(
        "www.nasa.gov",
        "feeds.bbci.co.uk"
    );

    private static final int MAX_FEED_BYTES = 2 * 1024 * 1024;
    private static final int MAX_FEED_ITEMS = 100;
    private static final int MAX_REDIRECTS = 3;

    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(5))
        .followRedirects(HttpClient.Redirect.NEVER)
        .build();

    private final ObjectMapper mapper;
    private final Path reportsDir;

    public TaskExecutor(
        ObjectMapper mapper,
        @Value("${app.reports-dir:/data/reports}") String dir) {
        this.mapper = mapper;
        this.reportsDir = Path.of(dir).toAbsolutePath().normalize();
    }

    public JsonNode execute(ExecutionJob job) throws Exception {
        JsonNode payload = mapper.readTree(
            job.payloadJson() == null ? "{}" : job.payloadJson());

        return switch (job.type()) {
            case "GENERATE_REPORT" -> generateReport(job, payload);
            case "SIMULATE_FAILURE" -> simulateFailure(job, payload);
            case "FETCH_RSS_FEED" -> fetchRssFeed(job, payload);
            default -> throw new IllegalArgumentException(
                "Unsupported job type: " + job.type());
        };
    }

    private JsonNode simulateFailure(ExecutionJob job, JsonNode payload) {
        int failFirst = Math.max(
            0, Math.min(8, payload.path("failFirstAttempts").asInt(2)));

        if (job.attempts() <= failFirst) {
            throw new IllegalStateException(
                "Intentional Failure Lab error on attempt "
                    + job.attempts() + " of " + failFirst);
        }

        ObjectNode result = mapper.createObjectNode();
        result.put("message",
            "Failure Lab job succeeded after simulated failures");
        result.put("attempts", job.attempts());
        result.put("sample", true);
        return result;
    }

    private JsonNode fetchRssFeed(
        ExecutionJob job, JsonNode payload) throws Exception {

        String rawUrl = payload.path("feedUrl").asText("").trim();
        URI feedUri = validateFeedUri(rawUrl);
        byte[] xml = downloadFeed(feedUri);

        DocumentBuilderFactory factory =
            DocumentBuilderFactory.newInstance();

        factory.setNamespaceAware(true);
        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        factory.setFeature(
            "http://apache.org/xml/features/disallow-doctype-decl",
            true);
        factory.setFeature(
            "http://xml.org/sax/features/external-general-entities",
            false);
        factory.setFeature(
            "http://xml.org/sax/features/external-parameter-entities",
            false);
        factory.setXIncludeAware(false);
        factory.setExpandEntityReferences(false);
        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");

        Document document = factory.newDocumentBuilder().parse(
            new ByteArrayInputStream(xml));

        Element root = document.getDocumentElement();
        Element channel = firstDirectChild(root, "channel");

        String feedTitle = channel != null
            ? directChildText(channel, "title")
            : directChildText(root, "title");

        List<Element> entries = new ArrayList<>();
        collectEntries(root, entries);

        if (entries.isEmpty()) {
            throw new IllegalArgumentException(
                "The response contains no RSS items or Atom entries");
        }

        Files.createDirectories(reportsDir);

        String fileName = job.id() + "-rss-feed.csv";
        Path target = reportsDir.resolve(fileName).normalize();

        if (!target.startsWith(reportsDir)) {
            throw new IllegalArgumentException("Invalid report path");
        }

        StringBuilder csv = new StringBuilder(
            "Title,Link,Published,Description\n");

        for (Element entry : entries) {
            String title = directChildText(entry, "title");
            String link = directChildText(entry, "link");
            String published = directChildText(
                entry, "pubdate", "published", "updated", "date");
            String description = directChildText(
                entry, "description", "summary", "content", "encoded");

            appendCsvRow(csv, title, link, published, description);
        }

        Files.writeString(
            target,
            csv.toString(),
            StandardCharsets.UTF_8,
            StandardOpenOption.CREATE,
            StandardOpenOption.TRUNCATE_EXISTING);

        ObjectNode result = mapper.createObjectNode();
        result.put("fileName", fileName);
        result.put("downloadPath", "/api/jobs/" + job.id() + "/result");
        result.put("feedTitle", feedTitle);
        result.put("sourceUrl", feedUri.toString());
        result.put("articleCount", entries.size());
        result.put("sampleData", false);

        return result;
    }

    private URI validateFeedUri(String rawUrl) {
        if (rawUrl == null || rawUrl.isBlank()) {
            throw new IllegalArgumentException(
                "payload.feedUrl is required");
        }

        final URI uri;
        try {
            uri = URI.create(rawUrl);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException(
                "Feed URL is invalid");
        }

        String host = uri.getHost();

        if (!"https".equalsIgnoreCase(uri.getScheme())
            || host == null
            || uri.getUserInfo() != null
            || uri.getFragment() != null
            || (uri.getPort() != -1 && uri.getPort() != 443)
            || !ALLOWED_FEED_HOSTS.contains(
            host.toLowerCase(Locale.ROOT))) {
            throw new IllegalArgumentException(
                "Feed URL must use HTTPS on an approved feed host");
        }

        return uri;
    }

    private byte[] downloadFeed(URI initialUri) throws Exception {
        URI currentUri = initialUri;

        for (int redirects = 0; redirects <= MAX_REDIRECTS; redirects++) {
            HttpRequest request = HttpRequest.newBuilder(currentUri)
                .timeout(Duration.ofSeconds(12))
                .header("User-Agent", "ForgeFlow-RSS-Reader/1.0")
                .header("Accept",
                    "application/rss+xml, application/atom+xml, "
                        + "application/xml, text/xml")
                .GET()
                .build();

            HttpResponse<InputStream> response = HTTP_CLIENT.send(
                request, HttpResponse.BodyHandlers.ofInputStream());

            int status = response.statusCode();

            if (status == 301 || status == 302 || status == 303
                || status == 307 || status == 308) {

                response.body().close();

                if (redirects == MAX_REDIRECTS) {
                    throw new IllegalStateException(
                        "Feed exceeded the redirect limit");
                }

                String location = response.headers()
                    .firstValue("Location")
                    .orElseThrow(() -> new IllegalStateException(
                        "Feed redirect has no Location header"));

                currentUri = validateFeedUri(
                    currentUri.resolve(location).toString());

                continue;
            }

            try (InputStream body = response.body()) {
                if (status != 200) {
                    throw new IllegalStateException(
                        "Feed server returned HTTP " + status);
                }

                long declaredLength = response.headers()
                    .firstValueAsLong("Content-Length")
                    .orElse(-1L);

                if (declaredLength > MAX_FEED_BYTES) {
                    throw new IllegalArgumentException(
                        "Feed exceeds the 2 MiB size limit");
                }

                byte[] bytes = body.readNBytes(MAX_FEED_BYTES + 1);

                if (bytes.length > MAX_FEED_BYTES) {
                    throw new IllegalArgumentException(
                        "Feed exceeds the 2 MiB size limit");
                }

                return bytes;
            }
        }

        throw new IllegalStateException("Unable to retrieve feed");
    }

    private void collectEntries(Element parent, List<Element> entries) {
        if (entries.size() >= MAX_FEED_ITEMS) {
            return;
        }

        for (Node node = parent.getFirstChild();
             node != null && entries.size() < MAX_FEED_ITEMS;
             node = node.getNextSibling()) {

            if (!(node instanceof Element element)) {
                continue;
            }

            String name = elementName(element);

            if ("item".equals(name) || "entry".equals(name)) {
                entries.add(element);
            } else {
                collectEntries(element, entries);
            }
        }
    }

    private Element firstDirectChild(Element parent, String wantedName) {
        for (Node node = parent.getFirstChild();
             node != null;
             node = node.getNextSibling()) {

            if (node instanceof Element element
                && wantedName.equals(elementName(element))) {
                return element;
            }
        }

        return null;
    }

    private String directChildText(
        Element parent, String... wantedNames) {

        for (Node node = parent.getFirstChild();
             node != null;
             node = node.getNextSibling()) {

            if (!(node instanceof Element element)) {
                continue;
            }

            String name = elementName(element);

            for (String wanted : wantedNames) {
                if (!wanted.equals(name)) {
                    continue;
                }

                if ("link".equals(name)) {
                    String href = element.getAttribute("href");

                    if (!href.isBlank()) {
                        return cleanText(href);
                    }
                }

                return cleanText(element.getTextContent());
            }
        }

        return "";
    }

    private String elementName(Element element) {
        String name = element.getLocalName();

        if (name == null || name.isBlank()) {
            name = element.getTagName();
        }

        int colon = name.lastIndexOf(':');
        if (colon >= 0) {
            name = name.substring(colon + 1);
        }

        return name.toLowerCase(Locale.ROOT);
    }

    private String cleanText(String value) {
        if (value == null) {
            return "";
        }

        return value
            .replaceAll("(?is)<script\\b[^>]*>.*?</script>", " ")
            .replaceAll("(?is)<style\\b[^>]*>.*?</style>", " ")
            .replaceAll("(?s)<[^>]+>", " ")
            .replace("&nbsp;", " ")
            .replaceAll("\\s+", " ")
            .trim();
    }

    private void appendCsvRow(
        StringBuilder csv,
        String title,
        String link,
        String published,
        String description) {

        csv.append(csvField(title)).append(',')
            .append(csvField(link)).append(',')
            .append(csvField(published)).append(',')
            .append(csvField(description)).append('\n');
    }

    private String csvField(String value) {
        String safe = cleanText(value);

        // Reduce spreadsheet formula injection from untrusted feed text.
        if (!safe.isEmpty()
            && "=+-@".indexOf(safe.charAt(0)) >= 0) {
            safe = "'" + safe;
        }

        return "\"" + safe.replace("\"", "\"\"") + "\"";
    }

    private JsonNode generateReport(
        ExecutionJob job, JsonNode payload) throws Exception {

        Files.createDirectories(reportsDir);

        int days = Math.max(
            1, Math.min(90, payload.path("lookbackDays").asInt(30)));

        String name = job.id() + "-sales-report.csv";
        Path target = reportsDir.resolve(name).normalize();

        if (!target.startsWith(reportsDir)) {
            throw new IllegalArgumentException("Invalid report path");
        }

        long totalOrders = 0, grossTotal = 0, refundTotal = 0;

        StringBuilder csv = new StringBuilder(
            "Record_Type,Job_ID,Data_Label,Date,Order_Count,"
                + "Gross_Sales_INR,Refunds_INR,Net_Sales_INR,Notes\n");

        csv.append("NOTICE,").append(job.id())
            .append(",SAMPLE DATA ONLY,,,,,,")
            .append("Illustrative data generated by ForgeFlow demo handler\n");

        LocalDate end = LocalDate.now(ZoneId.of("Asia/Kolkata"));
        LocalDate start = end.minusDays(days - 1L);

        for (int i = 0; i < days; i++) {
            LocalDate date = start.plusDays(i);
            long orders = 20 + (i * 7L) % 37;
            long gross = orders * (1200 + (i * 37L) % 800);
            long refunds = (i * 53L) % 1500;
            long net = gross - refunds;

            totalOrders += orders;
            grossTotal += gross;
            refundTotal += refunds;

            csv.append("DAILY,").append(job.id())
                .append(",SAMPLE DATA ONLY,").append(date).append(',')
                .append(orders).append(',').append(gross).append(',')
                .append(refunds).append(',').append(net)
                .append(",Illustrative values\n");
        }

        csv.append("TOTAL,").append(job.id())
            .append(",SAMPLE DATA ONLY,").append(start)
            .append(" to ").append(end).append(',')
            .append(totalOrders).append(',').append(grossTotal).append(',')
            .append(refundTotal).append(',')
            .append(grossTotal - refundTotal)
            .append(",Totals for all sample days\n");

        Files.writeString(
            target,
            csv.toString(),
            StandardCharsets.UTF_8,
            StandardOpenOption.CREATE,
            StandardOpenOption.TRUNCATE_EXISTING);

        ObjectNode result = mapper.createObjectNode();
        result.put("fileName", name);
        result.put("downloadPath", "/api/jobs/" + job.id() + "/result");
        result.put("sampleData", true);
        result.put("lookbackDays", days);
        result.put("totalOrders", totalOrders);
        result.put("grossSalesInr", grossTotal);
        result.put("refundsInr", refundTotal);
        result.put("netSalesInr", grossTotal - refundTotal);

        return result;
    }
}
