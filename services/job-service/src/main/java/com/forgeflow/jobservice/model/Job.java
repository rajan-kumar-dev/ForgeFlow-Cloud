package com.forgeflow.jobservice.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name="jobs", indexes={@Index(name="idx_jobs_status_run_at", columnList="status,run_at"), @Index(name="idx_jobs_owner_created", columnList="owner_id,created_at")}, uniqueConstraints=@UniqueConstraint(name="uk_jobs_owner_idempotency", columnNames={"owner_id","idempotency_key"}))
public class Job {
    @Id @Column(length=36) private String id;
    @Column(nullable=false, length=80) private String type;
    @Column(nullable=false, length=30) private String priority;
    @Enumerated(EnumType.STRING) @Column(nullable=false, length=24) private JobStatus status;
    @Column(name="owner_id", nullable=false, length=160) private String ownerId;
    @Column(name="idempotency_key", length=160) private String idempotencyKey;
    @Column(columnDefinition="text", nullable=false) private String payloadJson;
    @Column(columnDefinition="text") private String resultJson;
    @Column(nullable=false) private int attempts;
    @Column(name="max_attempts", nullable=false) private int maxAttempts;
    @Column(name="run_at", nullable=false) private Instant runAt;
    @Column(name="locked_by", length=160) private String lockedBy;
    @Column(name="lease_expires_at") private Instant leaseExpiresAt;
    @Column(name="started_at") private Instant startedAt;
    @Column(name="completed_at") private Instant completedAt;
    @Column(name="last_error", columnDefinition="text") private String lastError;
    @Column(name="created_at", nullable=false) private Instant createdAt;
    @Column(name="updated_at", nullable=false) private Instant updatedAt;

    protected Job() {}
    public Job(String id, String type, String priority, JobStatus status, String ownerId, String idempotencyKey,
               String payloadJson, int maxAttempts, Instant runAt) {
        this.id=id; this.type=type; this.priority=priority; this.status=status; this.ownerId=ownerId;
        this.idempotencyKey=idempotencyKey; this.payloadJson=payloadJson; this.maxAttempts=maxAttempts;
        this.runAt=runAt; this.attempts=0; this.createdAt=Instant.now(); this.updatedAt=this.createdAt;
    }
    @PreUpdate void beforeUpdate() { updatedAt=Instant.now(); }
    public String getId(){return id;} public String getType(){return type;} public String getPriority(){return priority;}
    public JobStatus getStatus(){return status;} public void setStatus(JobStatus v){status=v;}
    public String getOwnerId(){return ownerId;} public String getIdempotencyKey(){return idempotencyKey;}
    public String getPayloadJson(){return payloadJson;} public String getResultJson(){return resultJson;} public void setResultJson(String v){resultJson=v;}
    public int getAttempts(){return attempts;} public void setAttempts(int v){attempts=v;} public int getMaxAttempts(){return maxAttempts;}
    public void setMaxAttempts(int v) {
        if (v < 1) {
            throw new IllegalArgumentException(
                "maxAttempts must be positive"
            );
        }
        this.maxAttempts = v;
    }
    public Instant getRunAt(){return runAt;} public void setRunAt(Instant v){runAt=v;}
    public String getLockedBy(){return lockedBy;} public void setLockedBy(String v){lockedBy=v;}
    public Instant getLeaseExpiresAt(){return leaseExpiresAt;} public void setLeaseExpiresAt(Instant v){leaseExpiresAt=v;}
    public Instant getStartedAt(){return startedAt;} public void setStartedAt(Instant v){startedAt=v;}
    public Instant getCompletedAt(){return completedAt;} public void setCompletedAt(Instant v){completedAt=v;}
    public String getLastError(){return lastError;} public void setLastError(String v){lastError=v;}
    public Instant getCreatedAt(){return createdAt;} public Instant getUpdatedAt(){return updatedAt;}
}
