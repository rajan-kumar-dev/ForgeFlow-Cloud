package com.forgeflow.jobservice.service;
import org.springframework.beans.factory.annotation.Value; import org.springframework.stereotype.Component; import org.springframework.web.server.ResponseStatusException; import org.springframework.http.HttpStatus;
@Component public class InternalTokenVerifier {
    @Value("${forgeflow.internal-token}") private String expected;
 public void verify(String supplied){if(supplied==null||!java.security.MessageDigest.isEqual(expected.getBytes(java.nio.charset.StandardCharsets.UTF_8),supplied.getBytes(java.nio.charset.StandardCharsets.UTF_8))) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Invalid internal service token");}
}
