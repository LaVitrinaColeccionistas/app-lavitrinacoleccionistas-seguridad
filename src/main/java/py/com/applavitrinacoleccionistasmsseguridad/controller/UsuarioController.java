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
import py.com.lavitrinacoleccionistas.dto.UsuarioCreateDTO;
import py.com.lavitrinacoleccionistas.dto.UsuarioDTO;
import py.com.lavitrinacoleccionistas.dto.UsuarioPageDTO;
import py.com.lavitrinacoleccionistas.dto.UsuarioUpdateDTO;

@Slf4j
@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class UsuarioController {
    private final UsuarioService service;

    @GetMapping
    public UsuarioPageDTO listar(@RequestParam(required = false) String email,
                                 @RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "20") int size) {
        return service.listar(email, PageRequest.of(page, size, Sort.by("id")));
    }

    @GetMapping("/{id}")
    public UsuarioDTO obtener(@PathVariable Long id) { return service.obtener(id); }

    @PostMapping
    public ResponseEntity<UsuarioDTO> crear(@Valid @RequestBody UsuarioCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(dto));
    }

    @PutMapping("/{id}")
    public UsuarioDTO actualizar(@PathVariable Long id, @Valid @RequestBody UsuarioUpdateDTO dto) {
        return service.actualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) { service.eliminar(id); }
}