package com.forgeflow.jobservice.repo;
import com.forgeflow.jobservice.model.WorkerHeartbeat; import org.springframework.data.jpa.repository.JpaRepository;
public interface WorkerHeartbeatRepository extends JpaRepository<WorkerHeartbeat,String> {}
