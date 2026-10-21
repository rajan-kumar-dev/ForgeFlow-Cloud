package com.forgeflow.workerservice.api;
import com.fasterxml.jackson.databind.JsonNode; import org.springframework.beans.factory.annotation.Value; import org.springframework.stereotype.Component; import org.springframework.web.client.RestClient; import java.util.Map;
@Component public class JobApiClient {
 private final RestClient client; private final String token;
 public record ExecutionJob(String id,String type,String payloadJson,int attempts,int maxAttempts,String priority){}
 public record ClaimResponse(boolean claimed,ExecutionJob job){}
 public JobApiClient(@Value("${app.job-service-url:http://localhost:8081}") String base,@Value("${app.internal-token}") String token){this.client=RestClient.builder().baseUrl(base).build();this.token=token;}
 public ClaimResponse claim(String id,String workerId){return client.post().uri("/internal/jobs/{id}/claim?workerId={worker}",id,workerId).header("X-Internal-Token",token).retrieve().body(ClaimResponse.class);}
 public void complete(String id,String workerId,JsonNode result){client.post().uri("/internal/jobs/{id}/complete",id).header("X-Internal-Token",token).body(Map.of("workerId",workerId,"result",result)).retrieve().toBodilessEntity();}
 public void fail(String id,String workerId,String error){client.post().uri("/internal/jobs/{id}/fail",id).header("X-Internal-Token",token).body(Map.of("workerId",workerId,"errorMessage",error)).retrieve().toBodilessEntity();}
 public void heartbeat(String workerId,int active){client.post().uri("/internal/workers/heartbeat").header("X-Internal-Token",token).body(Map.of("workerId",workerId,"activeJobs",active)).retrieve().toBodilessEntity();}
}
