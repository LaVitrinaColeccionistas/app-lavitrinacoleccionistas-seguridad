package py.com.applavitrinacoleccionistasmsseguridad.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import py.com.applavitrinacoleccionistasmsseguridad.config.JwtProps;
import py.com.lavitrinacoleccionistas.entity.Usuario;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class JwtService {
    private final JwtEncoder encoder;
    private final JwtProps props;

    public String generar(Usuario u) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(props.issuer()).subject(u.getEmail())
                .issuedAt(now).expiresAt(now.plus(props.expiration()))
                .claim("uid", u.getId())
                .claim("roles", u.getRoles().stream().map(r -> r.getTipoRol().name()).toList())
                .build();
        return encoder.encode(JwtEncoderParameters.from(
                JwsHeader.with(MacAlgorithm.HS256).build(), claims)).getTokenValue();
    }
}