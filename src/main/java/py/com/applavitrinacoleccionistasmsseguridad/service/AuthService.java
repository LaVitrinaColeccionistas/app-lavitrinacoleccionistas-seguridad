package py.com.applavitrinacoleccionistasmsseguridad.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import py.com.applavitrinacoleccionistasmsseguridad.repository.IUsuarioRepository;
import py.com.applavitrinacoleccionistasmsseguridad.security.JwtService;
import py.com.lavitrinacoleccionistas.dto.LoginDTO;
import py.com.lavitrinacoleccionistas.dto.TokenDTO;
import py.com.lavitrinacoleccionistas.entity.Usuario;
import py.com.lavitrinacoleccionistas.enums.EstadoUsuario;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final IUsuarioRepository repo;
    private final PasswordEncoder encoder;
    private final JwtService jwt;
    private final JwtDecoder decoder;

    @Transactional(readOnly = true)
    public TokenDTO login(LoginDTO dto) {
        Usuario u = repo.findByEmail(dto.getEmail())
                .filter(x -> x.getEstado() == EstadoUsuario.ACTIVO)
                .filter(x -> encoder.matches(dto.getPassword(), x.getPassword()))
                .orElseThrow(() -> new BadCredentialsException("Credenciales inválidas"));
        TokenDTO t = new TokenDTO();
        t.setAccessToken(jwt.generar(u));
        t.setTokenType("Bearer");
        log.info("Login correcto uid={}", u.getId());
        return t;
    }

    public Map<String, Object> validar(String token) {
        return decoder.decode(token).getClaims();
    }
}