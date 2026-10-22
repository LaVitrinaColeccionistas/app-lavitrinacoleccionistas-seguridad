package py.com.applavitrinacoleccionistasmsseguridad.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import py.com.lavitrinacoleccionistas.entity.Usuario;

import java.util.Optional;

public interface IUsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    boolean existsByEmail(String email);
    Page<Usuario> findByEmailContainingIgnoreCase(String email, Pageable pageable);
}
