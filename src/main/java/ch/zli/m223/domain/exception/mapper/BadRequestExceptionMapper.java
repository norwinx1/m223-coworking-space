package ch.zli.m223.domain.exception.mapper;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import ch.zli.m223.domain.model.ApiError;

// According to https://docs.jboss.org/resteasy/docs/3.15.1.Final/userguide/html/ExceptionHandling.html

@Provider
public class BadRequestExceptionMapper implements ExceptionMapper<BadRequestException> {
    @Override
    public Response toResponse(BadRequestException exception) {
        ApiError apiError = new ApiError(Status.BAD_REQUEST, exception.getMessage());
        return Response.status(Status.BAD_REQUEST).entity(apiError).build();
    }
}
