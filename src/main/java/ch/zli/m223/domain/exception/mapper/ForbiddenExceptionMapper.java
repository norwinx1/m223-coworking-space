package ch.zli.m223.domain.exception.mapper;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import ch.zli.m223.domain.model.ApiError;
import io.quarkus.security.ForbiddenException;

@Provider
public class ForbiddenExceptionMapper implements ExceptionMapper<ForbiddenException> {
    @Override
    public Response toResponse(ForbiddenException exception) {
        ApiError apiError = new ApiError(Status.FORBIDDEN, exception.getMessage());
        return Response.status(Status.FORBIDDEN).entity(apiError).build();
    }
}
