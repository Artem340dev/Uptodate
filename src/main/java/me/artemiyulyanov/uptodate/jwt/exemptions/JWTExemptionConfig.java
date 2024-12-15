package me.artemiyulyanov.uptodate.jwt.exemptions;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JWTExemptionConfig {
    @Bean
    public JWTExemptionManager jwtExemptionManager() {
        return new JWTExemptionManager()
                .exempt(request -> request.getRequestURI().startsWith("/auth") &&
                                   !request.getRequestURI().endsWith("/logout"))
                .exempt(request -> request.getRequestURI().startsWith("/api/files"));
    }
}