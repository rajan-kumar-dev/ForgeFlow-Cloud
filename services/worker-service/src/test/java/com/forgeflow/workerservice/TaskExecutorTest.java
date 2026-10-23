package com.forgeflow.workerservice;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.forgeflow.workerservice.api.JobApiClient.ExecutionJob;
import com.forgeflow.workerservice.service.TaskExecutor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class TaskExecutorTest {
    @TempDir Path tempDir;

    @Test void reportIsGeneratedAndExplicitlyMarkedAsSampleData() throws Exception {
        TaskExecutor executor = new TaskExecutor(new ObjectMapper(), tempDir.toString());
        ExecutionJob job = new ExecutionJob("test-job-1", "GENERATE_REPORT", "{\"lookbackDays\":3}", 1, 3, "NORMAL");
        JsonNode result = executor.execute(job);
        assertEquals(true, result.path("sampleData").asBoolean());
        Path report = tempDir.resolve(result.path("fileName").asText());
        assertTrue(Files.exists(report));
        assertTrue(Files.readString(report).contains("SAMPLE DATA ONLY"));
    }

    @Test void failureLabFailsForConfiguredAttempts() {
        TaskExecutor executor = new TaskExecutor(new ObjectMapper(), tempDir.toString());
        ExecutionJob job = new ExecutionJob("test-job-2", "SIMULATE_FAILURE", "{\"failFirstAttempts\":2}", 1, 3, "NORMAL");
        assertThrows(IllegalStateException.class, () -> executor.execute(job));
    }

    @Test void failureLabRecoversAfterConfiguredAttempts() throws Exception {
        TaskExecutor executor = new TaskExecutor(new ObjectMapper(), tempDir.toString());
        ExecutionJob job = new ExecutionJob("test-job-3", "SIMULATE_FAILURE", "{\"failFirstAttempts\":2}", 3, 3, "NORMAL");
        JsonNode result = executor.execute(job);
        assertEquals(3, result.path("attempts").asInt());
    }
    @Test
    void rssFeedRejectsHttpUrl() {
        TaskExecutor executor =
            new TaskExecutor(new ObjectMapper(), tempDir.toString());

        ExecutionJob job = new ExecutionJob(
            "test-rss-http",
            "FETCH_RSS_FEED",
            "{\"feedUrl\":\"http://www.nasa.gov/feed/\"}",
            1,
            3,
            "NORMAL"
        );

        assertThrows(
            RuntimeException.class,
            () -> executor.execute(job)
        );
    }

    @Test
    void rssFeedRejectsHostOutsideAllowlist() {
        TaskExecutor executor =
            new TaskExecutor(new ObjectMapper(), tempDir.toString());

        ExecutionJob job = new ExecutionJob(
            "test-rss-host",
            "FETCH_RSS_FEED",
            "{\"feedUrl\":\"https://example.com/feed.xml\"}",
            1,
            3,
            "NORMAL"
        );

        assertThrows(
            RuntimeException.class,
            () -> executor.execute(job)
        );
    }
}
