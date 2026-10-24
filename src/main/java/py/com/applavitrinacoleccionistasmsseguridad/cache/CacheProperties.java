package py.com.applavitrinacoleccionistasmsseguridad.cache;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@ConfigurationProperties(prefix = "app.cache")
public record CacheProperties(
        @DefaultValue("redis") String tipo,
        @DefaultValue("lvc:seguridad:") String prefijo,
        @DefaultValue("10m") Duration ttlDefecto,
        @DefaultValue Dominios dominios,
        @DefaultValue Map<String, Duration> ttl,
        @DefaultValue({"py.com.lavitrinacoleccionistas.", "java."}) List<String> paquetesPermitidos) {

    public record Dominios(@DefaultValue("usuario") String usuario, @DefaultValue("rol") String rol) {
    }
}
