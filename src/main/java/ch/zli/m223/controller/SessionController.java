package ch.zli.m223.controller;

import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.reactive.RestResponse.ResponseBuilder;

import ch.zli.m223.domain.entity.ApplicationUser;
import ch.zli.m223.service.SessionService;

@Path("/login")
public class SessionController {
    @Inject
    SessionService sessionService;

    @POST
    @PermitAll
    @Consumes(MediaType.APPLICATION_JSON)
    public Response login(ApplicationUser applicationUser) {
        try {
            String token = sessionService.checkCredentials(applicationUser);
            return ResponseBuilder.ok("", MediaType.APPLICATION_JSON)
                    .header("Authorization", token)
                    .build().toResponse();
        } catch (SecurityException e) {
            return ResponseBuilder.create(401, e.getMessage()).build().toResponse();
        }
    }
}
