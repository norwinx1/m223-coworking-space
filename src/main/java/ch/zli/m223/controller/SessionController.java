package ch.zli.m223.controller;

import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.reactive.RestResponse.ResponseBuilder;

import ch.zli.m223.domain.entity.ApplicationUser;
import ch.zli.m223.domain.entity.Role;
import ch.zli.m223.domain.exception.EmailNotUniqueException;
import ch.zli.m223.service.ApplicationUserService;
import ch.zli.m223.service.SessionService;

@Path("/")
public class SessionController {
    @Inject
    SessionService sessionService;

    @Inject
    ApplicationUserService applicationUserService;

    @POST
    @Path("login")
    @PermitAll
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(ApplicationUser applicationUser) throws SecurityException {
        String token = sessionService.checkCredentials(applicationUser);
        return ResponseBuilder.ok("", MediaType.APPLICATION_JSON)
                .header("Authorization", token)
                .build().toResponse();
    }

    @POST
    @Path("register")
    @PermitAll
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ApplicationUser register(ApplicationUser applicationUser) throws EmailNotUniqueException {
        if (applicationUserService.count() == 0) {
            applicationUser.setRole(Role.ADMIN);
        } else {
            applicationUser.setRole(Role.MEMBER);
        }
            return applicationUserService.createApplicationUser(applicationUser);
    }
}
