package py.com.applavitrinacoleccionistasmsseguridad.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import py.com.applavitrinacoleccionistasmsseguridad.mapper.IUsuarioMapper;
import py.com.applavitrinacoleccionistasmsseguridad.repository.IRolRepository;
import py.com.lavitrinacoleccionistas.dto.RolDTO;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RolService {
    private final IRolRepository repo;
    private final IUsuarioMapper mapper;

    @Transactional(readOnly = true)
    public List<RolDTO> listar() { return repo.findAll().stream().map(mapper::toDto).toList(); }

    @Transactional(readOnly = true)
    public RolDTO obtener(Long id) {
        return mapper.toDto(repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Rol no encontrado: " + id)));
    }
}