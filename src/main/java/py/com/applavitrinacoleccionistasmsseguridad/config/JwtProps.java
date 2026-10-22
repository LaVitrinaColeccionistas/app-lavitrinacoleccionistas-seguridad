package py.com.applavitrinacoleccionistasmsseguridad.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "app.security.jwt")
public record JwtProps(String secret, Duration expiration, String issuer) {}