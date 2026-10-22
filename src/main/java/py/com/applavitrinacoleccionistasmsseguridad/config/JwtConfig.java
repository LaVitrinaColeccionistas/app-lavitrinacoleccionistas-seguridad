package py.com.applavitrinacoleccionistasmsseguridad.config;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Configuration
@EnableConfigurationProperties(JwtProps.class)
public class JwtConfig {
    @Bean
    SecretKey jwtKey(JwtProps p) {
        return new SecretKeySpec(Base64.getDecoder().decode(p.secret()), "HmacSHA256");
    }
    @Bean
    JwtEncoder jwtEncoder(SecretKey k) { return new NimbusJwtEncoder(new ImmutableSecret<>(k)); }
    @Bean
    JwtDecoder jwtDecoder(SecretKey k) {
        return NimbusJwtDecoder.withSecretKey(k).macAlgorithm(MacAlgorithm.HS256).build();
    }
    @Bean
    PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }
}