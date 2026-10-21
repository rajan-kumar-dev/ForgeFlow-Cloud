package com.forgeflow.jobservice.model;
import jakarta.persistence.*;
import java.time.Instant;
@Entity @Table(name="job_attempts", indexes=@Index(name="idx_attempt_job", columnList="job_id,attempt_number"))
public class JobAttempt {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(name="job_id",nullable=false,length=36) private String jobId;
 @Column(name="attempt_number",nullable=false) private int attemptNumber;
 @Column(name="worker_id",length=160) private String workerId;
 @Column(nullable=false,length=24) private String status;
 @Column(name="started_at") private Instant startedAt;
 @Column(name="finished_at",nullable=false) private Instant finishedAt;
 @Column(name="error_message",columnDefinition="text") private String errorMessage;
 protected JobAttempt(){}
 public JobAttempt(String jobId,int attemptNumber,String workerId,String status,Instant startedAt,Instant finishedAt,String errorMessage){this.jobId=jobId;this.attemptNumber=attemptNumber;this.workerId=workerId;this.status=status;this.startedAt=startedAt;this.finishedAt=finishedAt;this.errorMessage=errorMessage;}
 public Long getId(){return id;} public String getJobId(){return jobId;} public int getAttemptNumber(){return attemptNumber;} public String getWorkerId(){return workerId;} public String getStatus(){return status;} public Instant getStartedAt(){return startedAt;} public Instant getFinishedAt(){return finishedAt;} public String getErrorMessage(){return errorMessage;}
}
