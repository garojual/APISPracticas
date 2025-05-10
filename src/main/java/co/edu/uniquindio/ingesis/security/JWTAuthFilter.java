package co.edu.uniquindio.ingesis.security;

import io.jsonwebtoken.Claims;
import jakarta.annotation.Priority;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import java.io.IOException;
import java.util.List;
import jakarta.ws.rs.core.PathSegment;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class JWTAuthFilter implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        List<PathSegment> pathSegments = requestContext.getUriInfo().getPathSegments();
        String firstSegment = pathSegments.isEmpty() ? "" : pathSegments.get(0).getPath();
        String secondSegment = pathSegments.size() > 1 ? pathSegments.get(1).getPath() : "";
        String method = requestContext.getMethod();

        // Permitir acceso sin autenticación a /usuarios (registro) y /usuarios/login (login)
        if (("usuarios".equals(firstSegment) && "POST".equals(method)) ||
                ("usuarios".equals(firstSegment) && "login".equals(secondSegment))) {
            return; // No aplicar filtro de autenticación
        }

        String authorizationHeader = requestContext.getHeaderString("Authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Acceso denegado: Token no proporcionado o inválido").build());
            return;
        }

        String token = authorizationHeader.substring("Bearer ".length());

        try {
            Claims claims = JWTUtil.validateToken(token); // Obtiene las reclamaciones (claims) del token
            String userEmail = claims.getSubject(); // El subject es el email
            String userRole = claims.get("rol", String.class); // Extrae el rol del token

            final SecurityContext currentSecurityContext = requestContext.getSecurityContext();
            requestContext.setSecurityContext(new AuthSecurityContext(userEmail, userRole, currentSecurityContext.isSecure()));

        } catch (Exception e) {
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Acceso denegado: Token inválido o expirado").build());
        }

    }
}