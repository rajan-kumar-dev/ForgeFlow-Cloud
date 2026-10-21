package com.forgeflow.jobservice.api;
import org.springframework.http.*; import org.springframework.security.access.AccessDeniedException; import org.springframework.web.bind.annotation.*; import org.springframework.web.server.ResponseStatusException; import java.time.Instant; import java.util.Map;
@RestControllerAdvice public class ApiExceptionHandler {
 @ExceptionHandler(ResponseStatusException.class) ResponseEntity<Map<String,Object>> response(ResponseStatusException ex){return ResponseEntity.status(ex.getStatusCode()).body(Map.of("timestamp",Instant.now().toString(),"status",ex.getStatusCode().value(),"error",ex.getReason()==null?"Request failed":ex.getReason()));}
 @ExceptionHandler(AccessDeniedException.class) ResponseEntity<Map<String,Object>> forbidden(AccessDeniedException ex){return ResponseEntity.status(403).body(Map.of("timestamp",Instant.now().toString(),"status",403,"error",ex.getMessage()));}
 @ExceptionHandler(Exception.class) ResponseEntity<Map<String,Object>> internal(Exception ex){return ResponseEntity.status(500).body(Map.of("timestamp",Instant.now().toString(),"status",500,"error","Unexpected server error"));}
}
