package py.com.applavitrinacoleccionistasmsseguridad.service;


import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import py.com.applavitrinacoleccionistasmsseguridad.cache.CacheDeObjetos;
import py.com.applavitrinacoleccionistasmsseguridad.cache.CacheDominio;
import py.com.applavitrinacoleccionistasmsseguridad.cache.CacheProperties;
import py.com.applavitrinacoleccionistasmsseguridad.cache.Claves;
import py.com.applavitrinacoleccionistasmsseguridad.cache.DominioCacheResolver;
import py.com.applavitrinacoleccionistasmsseguridad.config.RegistroProps;
import py.com.applavitrinacoleccionistasmsseguridad.exception.ConflictException;
import py.com.applavitrinacoleccionistasmsseguridad.mapper.IUsuarioMapper;
import py.com.applavitrinacoleccionistasmsseguridad.repository.IRolRepository;
import py.com.applavitrinacoleccionistasmsseguridad.repository.IUsuarioRepository;
import py.com.lavitrinacoleccionistas.dto.*;
import py.com.lavitrinacoleccionistas.entity.Rol;
import py.com.lavitrinacoleccionistas.entity.Usuario;
import py.com.lavitrinacoleccionistas.enums.EstadoUsuario;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
@EnableConfigurationProperties(RegistroProps.class)
public class UsuarioService implements CacheDominio {
    private final IUsuarioRepository repo;
    private final IRolRepository rolRepo;
    private final PasswordEncoder encoder;
    private final IUsuarioMapper mapper;
    private final CacheProperties cacheProps;
    private final CacheDeObjetos cache;
    private final RegistroProps registro;

    @Override
    public String dominioCache() {
        return cacheProps.dominios().usuario();
    }

    @Transactional
    @CachePut(cacheResolver = DominioCacheResolver.NOMBRE, key = Claves.POR_RESULTADO)
    public UsuarioDTO registrar(RegistroDTO dto) {
        if (repo.existsByEmail(dto.getEmail())) throw new ConflictException("El email ya está registrado");
        Rol rol = rolRepo.findByTipoRol(registro.rol())
                .orElseThrow(() -> new IllegalStateException("Rol de registro no configurado en la BD: " + registro.rol()));
        Usuario u = new Usuario();
        u.setEmail(dto.getEmail());
        u.setPassword(encoder.encode(dto.getPassword()));
        u.setEstado(registro.estado());
        u.setRoles(new HashSet<>(Set.of(rol)));
        UsuarioDTO creado = mapper.toDto(repo.save(u));
        log.info("Registro de usuario id={} estado={}", creado.getId(), registro.estado());
        return creado;
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheResolver = DominioCacheResolver.NOMBRE, key = Claves.POR_ID)
    public UsuarioDTO obtener(Long id) {
        return mapper.toDto(buscar(id));
    }

    @Transactional(readOnly = true)
    public UsuarioPageDTO listar(String email, Pageable pageable) {
        Page<UsuarioDTO> p = repo.findByEmailContainingIgnoreCase(email == null ? "" : email, pageable)
                .map(mapper::toDto);
        p.getContent().forEach(u -> cache.guardar(dominioCache(), u.getId(), u));
        UsuarioPageDTO out = new UsuarioPageDTO();
        out.setContent(p.getContent());
        out.setTotalElements(p.getTotalElements());
        out.setTotalPages(p.getTotalPages());
        out.setSize(p.getSize());
        out.setNumber(p.getNumber());
        return out;
    }

    @Transactional
    @CachePut(cacheResolver = DominioCacheResolver.NOMBRE, key = Claves.POR_RESULTADO)
    public UsuarioDTO crear(UsuarioCreateDTO dto) {
        if (repo.existsByEmail(dto.getEmail())) throw new ConflictException("El email ya está registrado");
        Usuario u = new Usuario();
        u.setEmail(dto.getEmail());
        u.setPassword(encoder.encode(dto.getPassword()));
        u.setEstado(EstadoUsuario.ACTIVO);
        u.setRoles(buscarRoles(dto.getIdRoles()));
        return mapper.toDto(repo.save(u));
    }

    @Transactional
    @CachePut(cacheResolver = DominioCacheResolver.NOMBRE, key = Claves.POR_ID)
    public UsuarioDTO actualizar(Long id, UsuarioUpdateDTO dto) {
        Usuario u = buscar(id);
        if (dto.getEmail() != null && !dto.getEmail().equalsIgnoreCase(u.getEmail())) {
            if (repo.existsByEmail(dto.getEmail())) throw new ConflictException("El email ya está registrado");
            u.setEmail(dto.getEmail());
        }
        if (dto.getPassword() != null) u.setPassword(encoder.encode(dto.getPassword()));
        if (dto.getEstado() != null) u.setEstado(EstadoUsuario.valueOf(dto.getEstado().name()));
        if (dto.getIdRoles() != null) u.setRoles(buscarRoles(dto.getIdRoles()));
        return mapper.toDto(repo.save(u));
    }

    @Transactional
    @CacheEvict(cacheResolver = DominioCacheResolver.NOMBRE, key = Claves.POR_ID)
    public void eliminar(Long id) {
        Usuario u = buscar(id);
        u.setEstado(EstadoUsuario.SUSPENDIDO);
        repo.save(u);
        log.info("Usuario {} suspendido", id);
    }

    private Usuario buscar(Long id) {
        return repo.findById(id).orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado: " + id));
    }

    private Set<Rol> buscarRoles(Set<Long> ids) {
        List<Rol> roles = rolRepo.findAllById(ids);
        if (roles.size() != ids.size()) throw new EntityNotFoundException("Algún rol no existe");
        return new HashSet<>(roles);
    }
}