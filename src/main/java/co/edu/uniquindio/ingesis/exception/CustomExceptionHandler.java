package co.edu.uniquindio.ingesis.exception;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;


@Provider
public class CustomExceptionHandler implements ExceptionMapper<Exception> {

        @Override
        public Response toResponse(Exception exception) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"" + exception.getMessage() + "\"}")
                    .build();
        }
    }

