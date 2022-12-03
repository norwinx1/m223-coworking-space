package ch.zli.m223.domain.exception.mapper;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import ch.zli.m223.domain.model.ApiError;

@Provider
public class SecurityExceptionMapper implements ExceptionMapper<SecurityException> {

    @Override
    public Response toResponse(SecurityException exception) {
        ApiError apiError = new ApiError(Status.UNAUTHORIZED, exception.getMessage());
        return Response.status(Status.UNAUTHORIZED).entity(apiError).build();
    }

}
