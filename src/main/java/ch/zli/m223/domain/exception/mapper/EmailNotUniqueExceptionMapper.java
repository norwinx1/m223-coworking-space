package ch.zli.m223.domain.exception.mapper;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import ch.zli.m223.domain.exception.EmailNotUniqueException;
import ch.zli.m223.domain.model.ApiError;

@Provider
public class EmailNotUniqueExceptionMapper implements ExceptionMapper<EmailNotUniqueException> {

    @Override
    public Response toResponse(EmailNotUniqueException exception) {
        ApiError apiError = new ApiError(Status.CONFLICT, exception.getMessage());
        return Response.status(Status.CONFLICT).entity(apiError).build();
    }

}
