package com.forgeflow.jobservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain appSecurity(
        HttpSecurity http,
        @Value("${forgeflow.security.enabled:false}") boolean enabled
    ) throws Exception {

        http.csrf(csrf -> csrf.disable());

        if (enabled) {
            http.authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/actuator/health",
                    "/actuator/health/**",
                    "/actuator/prometheus"
                ).permitAll()
                .requestMatchers("/internal/**").permitAll()
                .anyRequest().authenticated()
            );

            http.oauth2ResourceServer(oauth2 ->
                oauth2.jwt(Customizer.withDefaults())
            );
        } else {
            // Local development mode only.
            http.authorizeHttpRequests(auth ->
                auth.anyRequest().permitAll()
            );
        }

        return http.build();
    }

    @Bean
    @ConditionalOnProperty(
        name = "forgeflow.security.enabled",
        havingValue = "true"
    )
    JwtDecoder jwtDecoder(
        @Value("${CLERK_ISSUER_URI}") String issuer
    ) {
        if (issuer == null || issuer.isBlank()) {
            throw new IllegalStateException(
                "CLERK_ISSUER_URI must be configured when security is enabled."
            );
        }

        return JwtDecoders.fromIssuerLocation(issuer);
    }
}
