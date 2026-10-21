package com.forgeflow.jobservice.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.forgeflow.jobservice.api.JobDtos.*;
import com.forgeflow.jobservice.model.Job;
import com.forgeflow.jobservice.model.JobStatus;
import com.forgeflow.jobservice.service.JobApplicationService;
import com.forgeflow.jobservice.service.OwnerResolver;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class JobController {
    private final JobApplicationService service;
    private final OwnerResolver owners;
    private final ObjectMapper mapper;

    @Value("${forgeflow.storage.reports-dir:/data/reports}")
    private String reportsDir;

    public JobController(JobApplicationService service, OwnerResolver owners, ObjectMapper mapper) {
        this.service = service;
        this.owners = owners;
        this.mapper = mapper;
    }

    @PostMapping("/jobs")
    public ResponseEntity<JobResponse> create(
            @RequestHeader(name = "Idempotency-Key", required = false) String key,
            @Valid @RequestBody CreateJobRequest body,
            Authentication auth) {
        CreateJobRequest request = body;
        String effectiveKey = key != null && !key.isBlank() ? key : body.idempotencyKey();
        if (!java.util.Objects.equals(effectiveKey, body.idempotencyKey())) {
            request = new CreateJobRequest(body.type(), body.payload(), body.priority(), body.scheduleMode(), body.runAt(), body.maxAttempts(), effectiveKey);
        }
        Job job = service.create(request, owners.ownerId(auth));
        return ResponseEntity.accepted().body(response(job));
    }

    @GetMapping("/jobs")
    public List<JobResponse> list(Authentication auth) {
        return service.list(owners.ownerId(auth), false).stream().map(this::response).toList();
    }

    @GetMapping("/jobs/{id}")
    public JobResponse get(@PathVariable String id, Authentication auth) {
        return response(service.get(id, owners.ownerId(auth), false));
    }

    @GetMapping("/jobs/{id}/attempts")
    public List<AttemptResponse> attempts(@PathVariable String id, Authentication auth) {
        return service.attempts(id, owners.ownerId(auth)).stream()
                .map(a -> new AttemptResponse(a.getId(), a.getJobId(), a.getAttemptNumber(), a.getWorkerId(), a.getStatus(), a.getStartedAt(), a.getFinishedAt(), a.getErrorMessage()))
                .toList();
    }

    @PostMapping("/jobs/{id}/cancel")
    public JobResponse cancel(@PathVariable String id, Authentication auth) {
        return response(service.cancel(id, owners.ownerId(auth)));
    }

    @GetMapping("/jobs/{id}/result")
    public ResponseEntity<FileSystemResource> result(@PathVariable String id, Authentication auth) {
        Job job = service.get(id, owners.ownerId(auth), false);
        if (job.getStatus() != JobStatus.SUCCEEDED || job.getResultJson() == null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Job result is not ready");
        }
        try {
            JsonNode node = mapper.readTree(job.getResultJson());
            String fileName = node.path("fileName").asText("");
            if (fileName.isBlank() || fileName.contains("/") || fileName.contains("\\")) {
                throw new IllegalArgumentException("Invalid result filename");
            }
            Path base = Path.of(reportsDir).toAbsolutePath().normalize();
            Path file = base.resolve(fileName).normalize();
            if (!file.startsWith(base) || !Files.isRegularFile(file)) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Report artifact not found");
            }
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("text/csv"))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .body(new FileSystemResource(file));
        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Report artifact not found");
        }
    }

    @GetMapping("/dashboard/summary")
    public Map<String, Object> summary(Authentication auth) {
        return service.summary(owners.ownerId(auth), false);
    }

    @GetMapping("/admin/summary")
    public Map<String, Object> adminSummary(Authentication auth) {
        owners.requireAdmin(auth);
        return service.summary(owners.ownerId(auth), true);
    }

    @GetMapping("/admin/jobs")
    public List<JobResponse> adminJobs(Authentication auth) {
        owners.requireAdmin(auth);
        return service.list(owners.ownerId(auth), true).stream().map(this::response).toList();
    }

    @GetMapping("/admin/workers")
    public List<WorkerResponse> adminWorkers(Authentication auth) {
        owners.requireAdmin(auth);
        return service.workerList().stream()
                .map(w -> new WorkerResponse(w.getWorkerId(), w.getActiveJobs(), w.getLastSeen(), w.getLastSeen().isAfter(Instant.now().minusSeconds(20))))
                .toList();
    }

    @PostMapping("/admin/jobs/{id}/retry")
    public JobResponse retry(@PathVariable String id, Authentication auth) {
        owners.requireAdmin(auth);
        return response(service.manualRetry(id));
    }

    private JobResponse response(Job job) {
        try {
            return new JobResponse(job.getId(), job.getType(), mapper.readTree(job.getPayloadJson()), job.getPriority(),
                    job.getStatus(), job.getOwnerId(), job.getAttempts(), job.getMaxAttempts(), job.getRunAt(),
                    job.getCreatedAt(), job.getUpdatedAt(), job.getStartedAt(), job.getCompletedAt(), job.getLastError(),
                    job.getResultJson() == null ? null : mapper.readTree(job.getResultJson()));
        } catch (Exception ex) {
            throw new IllegalStateException("Stored job JSON could not be read", ex);
        }
    }
}
