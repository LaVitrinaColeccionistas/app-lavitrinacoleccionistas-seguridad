package py.com.applavitrinacoleccionistasmsseguridad.mapper;

import org.mapstruct.Mapper;
import py.com.lavitrinacoleccionistas.dto.RolDTO;
import py.com.lavitrinacoleccionistas.dto.UsuarioDTO;
import py.com.lavitrinacoleccionistas.entity.Rol;
import py.com.lavitrinacoleccionistas.entity.Usuario;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;

@Mapper(componentModel = "spring")
public interface IUsuarioMapper {
    UsuarioDTO toDto(Usuario u);
    RolDTO toDto(Rol r);

    default OffsetDateTime toOffset(LocalDateTime v) {
        return v == null ? null : v.atZone(ZoneId.systemDefault()).toOffsetDateTime();
    }
}