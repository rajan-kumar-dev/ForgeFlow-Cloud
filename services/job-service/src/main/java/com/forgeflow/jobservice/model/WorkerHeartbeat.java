package com.forgeflow.jobservice.model;
import jakarta.persistence.*; import java.time.Instant;
@Entity @Table(name="worker_heartbeats")
public class WorkerHeartbeat {
 @Id @Column(name="worker_id",length=160) private String workerId;
 @Column(name="active_jobs",nullable=false) private int activeJobs;
 @Column(name="last_seen",nullable=false) private Instant lastSeen;
 protected WorkerHeartbeat(){}
 public WorkerHeartbeat(String workerId,int activeJobs,Instant lastSeen){this.workerId=workerId;this.activeJobs=activeJobs;this.lastSeen=lastSeen;}
 public String getWorkerId(){return workerId;} public int getActiveJobs(){return activeJobs;} public Instant getLastSeen(){return lastSeen;}
}
