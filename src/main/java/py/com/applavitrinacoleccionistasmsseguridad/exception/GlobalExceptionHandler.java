package py.com.applavitrinacoleccionistasmsseguridad.exception;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import java.util.Map;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({BadCredentialsException.class, JwtException.class})
    ResponseEntity<ErrorResponse> auth(Exception e) { return build(401, "Credenciales o token inválido"); }

    @ExceptionHandler(AuthenticationException.class)
    ResponseEntity<ErrorResponse> authn(Exception e) { return build(401, "Token inválido, expirado o ausente"); }

    @ExceptionHandler(AccessDeniedException.class)
    ResponseEntity<ErrorResponse> denied(Exception e) { return build(403, "No tienes permisos para este recurso"); }

    @ExceptionHandler(NoResourceFoundException.class)
    ResponseEntity<ErrorResponse> noResource(NoResourceFoundException e) {
        return build(404, "Recurso no encontrado");
    }

    @ExceptionHandler(EntityNotFoundException.class)
    ResponseEntity<ErrorResponse> notFound(EntityNotFoundException e) { return build(404, e.getMessage()); }

    @ExceptionHandler(ConflictException.class)
    ResponseEntity<ErrorResponse> conflict(ConflictException e) { return build(409, e.getMessage()); }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ErrorResponse> invalid(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(f -> {
                    if ("email".equals(f.getField()) && "Pattern".equals(f.getCode())) {
                        return "email: debe tener un formato de correo electrónico válido";
                    }
                    if ("idRoles".equals(f.getField()) && "Size".equals(f.getCode())) {
                        return "idRoles: debes seleccionar al menos un rol";
                    }
                    return f.getField() + ": " + f.getDefaultMessage();
                })
                .collect(Collectors.joining("; "));
        return build(400, msg);
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ErrorResponse> generic(Exception e) {
        log.error("Error no controlado", e);
        return build(500, "Error interno del servidor");
    }

    private ResponseEntity<ErrorResponse> build(int status, String msg) {
        return ResponseEntity.status(status).body(new ErrorResponse(status, msg));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<ErrorResponse> argumentoInvalido(IllegalArgumentException e) {
        return build(400, e.getMessage());
    }
}