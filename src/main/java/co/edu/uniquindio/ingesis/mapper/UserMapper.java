package co.edu.uniquindio.ingesis.mapper;

import co.edu.uniquindio.ingesis.dto.UserResponse;
import co.edu.uniquindio.ingesis.model.User;
import co.edu.uniquindio.ingesis.dto.UserRegistrationRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "jakarta")
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "id", ignore = true) // El ID no se mapea, porque lo genera la BD
    User toEntity(UserRegistrationRequest request);

    UserResponse toResponse(User user);
}
