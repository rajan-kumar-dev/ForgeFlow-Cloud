package com.forgeflow.jobservice.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class OwnerResolver {

    @Value("${forgeflow.security.enabled:false}")
    private boolean securityEnabled;

    @Value("${forgeflow.security.admin-claim:forgeflow_role}")
    private String adminClaim;

    /**
     * Resolves the owner from the authenticated JWT subject.
     * The subject is the Clerk user ID.
     */
    public String ownerId(Authentication auth) {
        if (auth instanceof JwtAuthenticationToken token
            && token.isAuthenticated()) {

            String subject = token.getToken().getSubject();

            if (subject != null && !subject.isBlank()) {
                return subject;
            }

            throw new AccessDeniedException(
                "Authenticated JWT subject is missing"
            );
        }

        if (securityEnabled) {
            throw new AccessDeniedException(
                "Authenticated Clerk user required"
            );
        }

        // Local development only. Never use this fallback in production.
        return "local-dev";
    }

    /**
     * Requires an explicit ForgeFlow administrator claim.
     */
    public void requireAdmin(Authentication auth) {
        if (!securityEnabled) {
            // Development mode only.
            return;
        }

        if (!(auth instanceof JwtAuthenticationToken token)
            || !token.isAuthenticated()) {
            throw new AccessDeniedException(
                "Admin authentication required"
            );
        }

        Object role = token.getToken()
            .getClaims()
            .get(adminClaim);

        if (!"admin".equals(role)) {
            throw new AccessDeniedException(
                "Admin role required"
            );
        }
    }
}
