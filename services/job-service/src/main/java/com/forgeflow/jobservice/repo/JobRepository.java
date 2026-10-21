package com.forgeflow.jobservice.repo;
import com.forgeflow.jobservice.model.*; import org.springframework.data.domain.Pageable; import org.springframework.data.jpa.repository.*; import org.springframework.data.repository.query.Param; import org.springframework.stereotype.Repository; import java.time.Instant; import java.util.*;
@Repository public interface JobRepository extends JpaRepository<Job,String> {
 Optional<Job> findByOwnerIdAndIdempotencyKey(String ownerId,String idempotencyKey);
 List<Job> findTop200ByOwnerIdOrderByCreatedAtDesc(String ownerId);
 List<Job> findTop200ByOrderByCreatedAtDesc();
 List<Job> findByStatusInAndRunAtLessThanEqualOrderByRunAtAsc(List<JobStatus> statuses, Instant now, Pageable pageable);
 List<Job> findByStatusAndLeaseExpiresAtBefore(JobStatus status,Instant now);
 long countByStatus(JobStatus status); long countByOwnerIdAndStatus(String ownerId,JobStatus status);
 @Modifying(clearAutomatically=true, flushAutomatically=true) @Query("update Job j set j.status=:runningStatus, j.attempts=j.attempts+1, j.lockedBy=:worker, j.leaseExpiresAt=:lease, j.startedAt=:now, j.updatedAt=:now where j.id=:id and j.status=:queuedStatus")
 int claim(@Param("id") String id,@Param("worker") String worker,@Param("lease") Instant lease,@Param("now") Instant now,@Param("runningStatus") JobStatus runningStatus,@Param("queuedStatus") JobStatus queuedStatus);
}
