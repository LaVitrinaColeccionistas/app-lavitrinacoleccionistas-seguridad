package py.com.applavitrinacoleccionistasmsseguridad.cache;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@ConfigurationProperties(prefix = "app.cache")
public record CacheProperties(
        String tipo,
        String prefijo,
        Duration ttlDefecto,
        Dominios dominios,
        @DefaultValue Map<String, Duration> ttl,
        List<String> paquetesPermitidos) {

    public record Dominios(String usuario, String rol) {
    }
}
