package py.com.applavitrinacoleccionistasmsseguridad.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import py.com.lavitrinacoleccionistas.enums.EstadoUsuario;
import py.com.lavitrinacoleccionistas.enums.TipoRol;

@ConfigurationProperties(prefix = "app.security.registro")
public record RegistroProps(TipoRol rol, EstadoUsuario estado) {}