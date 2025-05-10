package co.edu.uniquindio.ingesis.dto;

import co.edu.uniquindio.ingesis.domain.Rol;

public record UserResponse(
        Long id,
        String usuario,
        String email,
        Rol rol
) {}
