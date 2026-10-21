package com.forgeflow.jobservice.api;
import com.fasterxml.jackson.databind.JsonNode; import com.forgeflow.jobservice.model.*; import java.time.Instant;
public final class JobDtos { private JobDtos(){}
 public record CreateJobRequest(String type, JsonNode payload, String priority, String scheduleMode, Instant runAt, Integer maxAttempts, String idempotencyKey) {}
 public record JobResponse(String id,String type,JsonNode payload,String priority,JobStatus status,String ownerId,int attempts,int maxAttempts,Instant runAt,Instant createdAt,Instant updatedAt,Instant startedAt,Instant completedAt,String lastError,JsonNode result) {}
 public record AttemptResponse(Long id,String jobId,int attemptNumber,String workerId,String status,Instant startedAt,Instant finishedAt,String errorMessage) {}
 public record JobMessage(String jobId) {}
 public record ClaimResponse(boolean claimed,ExecutionJob job) {}
 public record ExecutionJob(String id,String type,String payloadJson,int attempts,int maxAttempts,String priority) {}
 public record CompleteRequest(String workerId,JsonNode result) {}
 public record FailRequest(String workerId,String errorMessage) {}
 public record HeartbeatRequest(String workerId,int activeJobs) {}
 public record WorkerResponse(String workerId,int activeJobs,Instant lastSeen,boolean healthy) {}
}
