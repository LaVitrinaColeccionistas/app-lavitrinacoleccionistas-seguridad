package py.com.applavitrinacoleccionistasmsseguridad.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import py.com.lavitrinacoleccionistas.entity.Rol;
import py.com.lavitrinacoleccionistas.enums.TipoRol;

import java.util.Optional;

public interface IRolRepository extends JpaRepository<Rol, Long> {
    Optional<Rol> findByTipoRol(TipoRol tipoRol);
}
