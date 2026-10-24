package py.com.applavitrinacoleccionistasmsseguridad.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import py.com.applavitrinacoleccionistasmsseguridad.cache.CacheDeObjetos;
import py.com.applavitrinacoleccionistasmsseguridad.cache.CacheDominio;
import py.com.applavitrinacoleccionistasmsseguridad.cache.CacheProperties;
import py.com.applavitrinacoleccionistasmsseguridad.cache.Claves;
import py.com.applavitrinacoleccionistasmsseguridad.cache.DominioCacheResolver;
import py.com.applavitrinacoleccionistasmsseguridad.mapper.IUsuarioMapper;
import py.com.applavitrinacoleccionistasmsseguridad.repository.IRolRepository;
import py.com.lavitrinacoleccionistas.dto.RolDTO;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RolService implements CacheDominio {
    private final IRolRepository repo;
    private final IUsuarioMapper mapper;
    private final CacheProperties cacheProps;
    private final CacheDeObjetos cache;

    @Override
    public String dominioCache() {
        return cacheProps.dominios().rol();
    }

    @Transactional(readOnly = true)
    public List<RolDTO> listar() {
        List<RolDTO> roles = repo.findAll().stream().map(mapper::toDto).toList();
        roles.forEach(r -> cache.guardar(dominioCache(), r.getId(), r));
        return roles;
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheResolver = DominioCacheResolver.NOMBRE, key = Claves.POR_ID)
    public RolDTO obtener(Long id) {
        return mapper.toDto(repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Rol no encontrado: " + id)));
    }
}