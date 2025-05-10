package co.edu.uniquindio.ingesis.controller;

import co.edu.uniquindio.ingesis.dto.UserRegistrationRequest;
import co.edu.uniquindio.ingesis.dto.UserResponse;
import co.edu.uniquindio.ingesis.mapper.UserMapper;
import co.edu.uniquindio.ingesis.model.TokenResponse;
import co.edu.uniquindio.ingesis.model.UserLoginRequest;
import co.edu.uniquindio.ingesis.security.JWTUtil;
import co.edu.uniquindio.ingesis.service.UserService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.ExampleObject;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;

import java.util.List;

@SecurityScheme(
        securitySchemeName = "jwtAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
@Path("/usuarios")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Gestión de Usuarios", description = "API para gestionar usuarios, incluyendo registro, login, actualización completa y parcial, eliminación e inicio de sesión para seguridad.")
public class UserController {

    @Inject
    UserService userService;

    @Inject
    UserMapper userMapper;

    @POST
    @Operation(summary = "Registra un nuevo usuario", description = "Crea un nuevo usuario en el sistema.")
    @APIResponse(responseCode = "201", description = "Usuario registrado exitosamente")
    @APIResponse(responseCode = "400", description = "Datos incompletos o formato inválido")
    @APIResponse(responseCode = "409", description = "Email o nombre de usuario ya registrado")
    @APIResponse(responseCode = "500", description = "Error en el servidor")
    public Response registerUser(
            @Valid @RequestBody(
                    description = "Datos del usuario a registrar",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserRegistrationRequest.class),
                            examples = @ExampleObject(
                                    name = "Ejemplo de Registro",
                                    summary = "Ejemplo de entrada para registrar un usuario",
                                    description = "Este es un ejemplo de cómo enviar los datos para registrar un usuario.",
                                    value = """
                                        {
                                            "email": "usuario@example.com",
                                            "clave": "Password123",
                                            "usuario": "usuarioEjemplo",
                                            "rol": "ADMIN"
                                        }
                                        """
                            )
                    )
            ) UserRegistrationRequest request) {
        try {
            UserResponse registeredUser = userService.registerUser(request);
            return Response.status(Response.Status.CREATED).entity(registeredUser).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Error en el servidor: " + e.getMessage() + "\" }")
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
    }


    @GET
    @SecurityRequirement(name = "jwtAuth") // Requiere autenticación con JWT
    @Operation(summary = "Obtiene la lista de todos los usuarios", description = "Retorna una lista paginada y filtrable de usuarios registrados.")
    @APIResponse(responseCode = "200", description = "Lista de usuarios devuelta exitosamente")
    @APIResponse(responseCode = "404", description = "No hay usuarios registrados")
    @APIResponse(responseCode = "500", description = "Error en el servidor")
    public Response getAllUsers(
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("limit") @DefaultValue("10") int limit,
            @QueryParam("email") String email) {
        try {
            List<UserResponse> users = userService.getAllUsers(page, limit, email);
            if (users.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND).entity("No hay usuarios registrados").build();
            }
            return Response.ok(users).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error en el servidor").build();
        }
    }

    @GET
    @Path("/{id}")
    @SecurityRequirement(name = "jwtAuth") // Requiere autenticación con JWT
    @Operation(summary = "Obtiene un usuario por ID", description = "Retorna los datos de un usuario específico.")
    @APIResponse(responseCode = "200", description = "Usuario encontrado")
    @APIResponse(responseCode = "404", description = "Usuario no encontrado")
    @APIResponse(responseCode = "500", description = "Error en el servidor")
    public Response getUserById(
            @Parameter(description = "ID del usuario a consultar") @PathParam("id") Long id) {
        try {
            UserResponse user = userService.getUserById(id);
            if (user == null) {
                return Response.status(Response.Status.NOT_FOUND).entity("Usuario no encontrado").build();
            }
            return Response.ok(user).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error en el servidor").build();
        }
    }

    @PUT
    @Path("/{id}")
    @SecurityRequirement(name = "jwtAuth") // Requiere autenticación con JWT
    @Operation(summary = "Actualiza un usuario existente (completo)", description = "Actualiza todos los campos de un usuario.")
    @APIResponse(responseCode = "200", description = "Usuario actualizado exitosamente")
    @APIResponse(responseCode = "400", description = "Datos incompletos o formato inválido")
    @APIResponse(responseCode = "404", description = "Usuario no encontrado")
    @APIResponse(responseCode = "500", description = "Error en el servidor")
    public Response updateUser(@PathParam("id") Long id, @Valid UserRegistrationRequest request) {
        try {
            UserResponse updatedUser = userService.updateUser(id, userMapper.toEntity(request));
            if (updatedUser == null) {
                return Response.status(Response.Status.NOT_FOUND).entity("Usuario no encontrado").build();
            }
            return Response.ok(updatedUser).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error en el servidor").build();
        }
    }

    @PATCH
    @Path("/{id}")
    @SecurityRequirement(name = "jwtAuth") // Requiere autenticación con JWT
    @Operation(summary = "Actualiza un usuario existente (parcial)", description = "Actualiza campos específicos de un usuario.")
    @APIResponse(responseCode = "200", description = "Usuario actualizado exitosamente")
    @APIResponse(responseCode = "400", description = "Datos incompletos o formato inválido")
    @APIResponse(responseCode = "404", description = "Usuario no encontrado")
    @APIResponse(responseCode = "500", description = "Error en el servidor")
    public Response partialUpdateUser(@PathParam("id") Long id, UserRegistrationRequest request) {
        try {
            UserResponse updatedUser = userService.partialUpdateUser(id, userMapper.toEntity(request));
            if (updatedUser == null) {
                return Response.status(Response.Status.NOT_FOUND).entity("Usuario no encontrado").build();
            }
            return Response.ok(updatedUser).build();
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("Usuario no encontrado")) {
                return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
            }
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            if (e.getMessage().contains("Usuario no encontrado")) {
                return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
            }
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error en el servidor").build();
        }
    }

    @DELETE
    @Path("/{id}")
    @SecurityRequirement(name = "jwtAuth") // Requiere autenticación con JWT
    @Operation(summary = "Elimina un usuario por ID", description = "Elimina un usuario del sistema.")
    @APIResponse(responseCode = "204", description = "Usuario eliminado exitosamente")
    @APIResponse(responseCode = "404", description = "Usuario no encontrado")
    @APIResponse(responseCode = "500", description = "Error en el servidor")
    public Response deleteUser(@PathParam("id") Long id) {
        try {
            boolean deleted = userService.deleteUser(id);
            if (!deleted) {
                return Response.status(Response.Status.NOT_FOUND).entity("Usuario no encontrado").build();
            }
            return Response.noContent().build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error en el servidor").build();
        }
    }

    @POST
    @Path("/login")
    @Tag(name = "Login")
    @Operation(summary = "Iniciar sesión", description = "Inicia sesión y genera el token para acceder a los demás endpoints.")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(@Valid UserLoginRequest loginRequest) {
        try {
            // Validar que los campos no estén vacíos
            if (loginRequest == null || loginRequest.getEmail() == null || loginRequest.getClave() == null) {
                return Response.status(Response.Status.BAD_REQUEST).entity("Solicitud inválida, faltan datos").build();
            }

            // Autenticar al usuario y obtener su rol
            String rol = userService.login(loginRequest.getEmail(), loginRequest.getClave());
            if (rol == null) {
                return Response.status(Response.Status.UNAUTHORIZED).entity("Credenciales incorrectas").build();
            }

            // Generar el token JWT con el email y el rol del usuario
            String jwtToken = JWTUtil.generateToken(loginRequest.getEmail(), rol);

            // Devolver el token en la respuesta
            return Response.ok().entity(new TokenResponse(jwtToken)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error en el servidor").build();
        }
    }
}