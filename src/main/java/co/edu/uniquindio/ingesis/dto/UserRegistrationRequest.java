package co.edu.uniquindio.ingesis.dto;

import co.edu.uniquindio.ingesis.domain.Rol;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.Objects;

public record UserRegistrationRequest(
        @NotBlank(message = "El campo es requerido")
        @Email(message = "Debe ser un email válido")
        String email,

        @NotBlank(message = "El campo es requerido")
        @Pattern(regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).*$", message = "Debe contener al menos un número, una letra minúscula y una mayúscula")
        @Size(min = 8, message = "La longitud mínima es 8")
        String clave,

        @NotBlank(message = "El campo es requerido")
        @Size(max = 100, message = "No debe exceder los 100 caracteres")
        String usuario,


        Rol rol // Campo para el rol del usuario
) {
    public UserRegistrationRequest {
        rol = Objects.requireNonNullElse(rol, Rol.USER); // Asigna USER como rol por defecto si no se proporciona
    }
}