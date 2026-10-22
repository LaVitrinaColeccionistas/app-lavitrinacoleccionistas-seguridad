package py.com.applavitrinacoleccionistasmsseguridad.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import py.com.applavitrinacoleccionistasmsseguridad.service.RolService;
import py.com.lavitrinacoleccionistas.dto.RolDTO;

import java.util.List;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class RolController {
    private final RolService service;

    @GetMapping
    public List<RolDTO> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public RolDTO obtener(@PathVariable Long id) {
        return service.obtener(id);
    }
}