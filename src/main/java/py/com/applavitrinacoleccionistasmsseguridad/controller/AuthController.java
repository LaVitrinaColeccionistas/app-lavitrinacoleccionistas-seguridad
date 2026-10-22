package py.com.applavitrinacoleccionistasmsseguridad.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import py.com.applavitrinacoleccionistasmsseguridad.service.AuthService;
import py.com.lavitrinacoleccionistas.dto.LoginDTO;
import py.com.lavitrinacoleccionistas.dto.TokenDTO;
import py.com.lavitrinacoleccionistas.dto.ValidarTokenDTO;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService service;

    @PostMapping("/login")
    public TokenDTO login(@Valid @RequestBody LoginDTO dto) { return service.login(dto); }

    @PostMapping("/validate")
    public Map<String, Object> validar(@Valid @RequestBody ValidarTokenDTO dto) {
        return service.validar(dto.getToken());
    }
}