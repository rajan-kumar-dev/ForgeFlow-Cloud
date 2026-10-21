package com.forgeflow.jobservice.api;
import com.fasterxml.jackson.databind.JsonNode; import com.forgeflow.jobservice.api.JobDtos.*; import com.forgeflow.jobservice.service.*; import org.springframework.http.HttpStatus; import org.springframework.web.bind.annotation.*; import org.springframework.web.server.ResponseStatusException; import java.util.Map;
@RestController @RequestMapping("/internal") public class InternalController {
 private final JobApplicationService service; private final InternalTokenVerifier tokens;
 public InternalController(JobApplicationService service,InternalTokenVerifier tokens){this.service=service;this.tokens=tokens;}
 private void auth(String token){tokens.verify(token);}
 @PostMapping("/jobs/{id}/claim") public ClaimResponse claim(@PathVariable String id,@RequestParam String workerId,@RequestHeader(name="X-Internal-Token",required=false) String token){auth(token);return service.claim(id,workerId);}
 @PostMapping("/jobs/{id}/complete") public Map<String,Object> complete(@PathVariable String id,@RequestBody CompleteRequest body,@RequestHeader(name="X-Internal-Token",required=false) String token){auth(token);service.complete(id,body.workerId(),body.result());return Map.of("ok",true);}
 @PostMapping("/jobs/{id}/fail") public Map<String,Object> fail(@PathVariable String id,@RequestBody FailRequest body,@RequestHeader(name="X-Internal-Token",required=false) String token){auth(token);service.fail(id,body.workerId(),body.errorMessage());return Map.of("ok",true);}
 @PostMapping("/workers/heartbeat") public Map<String,Object> heartbeat(@RequestBody HeartbeatRequest body,@RequestHeader(name="X-Internal-Token",required=false) String token){auth(token);service.heartbeat(body.workerId(),body.activeJobs());return Map.of("ok",true);}
 @PostMapping("/scheduler/dispatch-due") public Map<String,Object> dispatch(@RequestHeader(name="X-Internal-Token",required=false) String token){auth(token);return Map.of("processed",service.dispatchDueAndRecover());}
}
