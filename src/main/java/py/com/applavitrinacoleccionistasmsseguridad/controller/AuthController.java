package py.com.applavitrinacoleccionistasmsseguridad.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import py.com.applavitrinacoleccionistasmsseguridad.service.AuthService;
import py.com.applavitrinacoleccionistasmsseguridad.service.UsuarioService;
import py.com.lavitrinacoleccionistas.dto.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService service;
    private final UsuarioService usuarioService;

    @PostMapping("/login")
    public TokenDTO login(@Valid @RequestBody LoginDTO dto) { return service.login(dto); }

    @PostMapping("/register")
    public ResponseEntity<UsuarioDTO> registrar(@Valid @RequestBody RegistroDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.registrar(dto));
    }

    @PostMapping("/validate")
    public Map<String, Object> validar(@Valid @RequestBody ValidarTokenDTO dto) {
        return service.validar(dto.getToken());
    }
}