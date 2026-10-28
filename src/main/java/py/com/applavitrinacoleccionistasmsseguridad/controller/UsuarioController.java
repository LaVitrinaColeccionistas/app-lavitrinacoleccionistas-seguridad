package py.com.applavitrinacoleccionistasmsseguridad.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import py.com.applavitrinacoleccionistasmsseguridad.service.UsuarioService;
import py.com.lavitrinacoleccionistas.dto.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService service;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public UsuarioPageDTO listar(@RequestParam(required = false) String email,
                                 @RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "20") int size) {
        return service.listar(email, PageRequest.of(page, size, Sort.by("id")));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public UsuarioDTO obtener(@PathVariable Long id) {
        return service.obtener(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UsuarioDTO> crear(@Valid @RequestBody UsuarioCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public UsuarioDTO actualizar(@PathVariable Long id,
                                 @Valid @RequestBody UsuarioUpdateDTO dto) {
        return service.actualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }

    @GetMapping("/nombres")
    @PreAuthorize("hasAnyRole('ADMIN', 'COMPRADOR', 'VENDEDOR')")
    public List<PerfilUsuarioDTO> obtenerNombres(@RequestParam List<Long> ids) {
        return service.obtenerNombres(ids);
    }
}