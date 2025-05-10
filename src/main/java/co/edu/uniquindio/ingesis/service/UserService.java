package co.edu.uniquindio.ingesis.service;

import co.edu.uniquindio.ingesis.model.User;
import co.edu.uniquindio.ingesis.dto.UserRegistrationRequest;
import co.edu.uniquindio.ingesis.dto.UserResponse;
import co.edu.uniquindio.ingesis.mapper.UserMapper;
import co.edu.uniquindio.ingesis.repository.UserRepository;
import co.edu.uniquindio.ingesis.security.JWTUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class UserService {

    @Inject
    UserRepository userRepository;

    @Inject
    UserMapper userMapper;

    @Transactional
    public UserResponse registerUser(UserRegistrationRequest request) {
        // Validaciones básicas
        if (request.email() == null || request.clave() == null || request.usuario() == null) {
            throw new IllegalArgumentException("Todos los campos son obligatorios.");
        }

        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new IllegalArgumentException("El email ya está registrado.");
        }

        // Convertir DTO a Entidad y hashear la clave
        User user = userMapper.toEntity(request);
        user.setClave(hashPassword(request.clave()));

        userRepository.persist(user);

        // Devolver un DTO en lugar de la entidad completa
        return userMapper.toResponse(user);
    }

    public List<UserResponse> getAllUsers(int page, int limit, String email) {
        List<User> users;
        if (email != null && !email.isEmpty()) {
            users = userRepository.find("email", email).list();
        } else {
            users = userRepository.findAll().page(page - 1, limit).list();
        }

        // Convertir lista de entidades a lista de DTOs
        return users.stream().map(userMapper::toResponse).toList();
    }

    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id);
        if (user == null) {
            throw new IllegalArgumentException("Usuario no encontrado.");
        }
        return userMapper.toResponse(user);
    }

    @Transactional
    public UserResponse updateUser(Long id, User user) {
        User existingUser = userRepository.findById(id);
        if (existingUser == null) {
            throw new IllegalArgumentException("Usuario no encontrado.");
        }

        // Actualizar campos
        existingUser.setUsuario(user.getUsuario());
        existingUser.setEmail(user.getEmail());
        existingUser.setClave(hashPassword(user.getClave()));
        existingUser.setRol(user.getRol());

        return userMapper.toResponse(existingUser);
    }

    @Transactional
    public UserResponse partialUpdateUser(Long id, User user) {
        User existingUser = userRepository.findById(id);
        if (existingUser == null) {
            throw new IllegalArgumentException("Usuario no encontrado.");
        }

        // Solo actualizar si los valores no son nulos
        if (user.getUsuario() != null) existingUser.setUsuario(user.getUsuario());
        if (user.getEmail() != null) existingUser.setEmail(user.getEmail());
        if (user.getClave() != null) existingUser.setClave(hashPassword(user.getClave()));
        if (user.getRol() != null) existingUser.setRol(user.getRol());

        return userMapper.toResponse(existingUser);
    }

    @Transactional
    public boolean deleteUser(Long id) {
        return userRepository.deleteById(id);
    }

    public String login(String email, String clave) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent() && BCrypt.checkpw(clave, user.get().getClave())) {
            return JWTUtil.generateToken(user.get().getEmail(), user.get().getRol().name());
        }
        throw new IllegalArgumentException("Credenciales incorrectas.");
    }

    private String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }
}
