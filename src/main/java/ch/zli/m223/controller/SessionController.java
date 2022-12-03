package ch.zli.m223.controller;

import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.resteasy.reactive.RestResponse.ResponseBuilder;

import ch.zli.m223.domain.entity.ApplicationUser;
import ch.zli.m223.domain.entity.Role;
import ch.zli.m223.domain.exception.ConflictException;
import ch.zli.m223.domain.model.Credentials;
import ch.zli.m223.service.ApplicationUserService;
import ch.zli.m223.service.SessionService;

@Path("/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Session", description = "Handling of the session")
@PermitAll
public class SessionController {
    @Inject
    SessionService sessionService;

    @Inject
    ApplicationUserService applicationUserService;

    @POST
    @Path("login")
    @Valid
    @Operation(summary = "Login with email and password", description = "If email and password are correct a JWT token is returned for further authenticated requests.")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Succesfully logged in"),
            @APIResponse(responseCode = "401", description = "Invalid login data")
    })
    public Response login(Credentials credentials) throws SecurityException {
        String token = sessionService.checkCredentials(credentials);
        return ResponseBuilder.ok("", MediaType.APPLICATION_JSON)
                .header("Authorization", token)
                .build().toResponse();
    }

    @POST
    @Path("register")
    @Valid
    @Operation(summary = "Register a new user", description = "Register a new user with firstname, lastname, email and password. The first registered user is automatically assigned the role admin. All further users wil have the role member.")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Succesfully registered new user"),
            @APIResponse(responseCode = "409", description = "Email already in use")
    })
    public ApplicationUser register(ApplicationUser applicationUser) throws ConflictException {
        if (applicationUserService.count() == 0) {
            applicationUser.setRole(Role.ADMIN);
        } else {
            applicationUser.setRole(Role.MEMBER);
        }
        return applicationUserService.createApplicationUser(applicationUser);
    }
}
