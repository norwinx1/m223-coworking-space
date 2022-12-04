package ch.zli.m223.controller;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import ch.zli.m223.domain.entity.ApplicationUser;
import ch.zli.m223.domain.exception.ConflictException;
import ch.zli.m223.service.ApplicationUserService;

@Path("/members")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Members", description = "Handling of members")
@RolesAllowed({ "ADMIN" })
public class MemberController {
    @Inject
    JsonWebToken jwt;

    @Inject
    ApplicationUserService applicationUserService;

    @POST
    @Valid
    @Operation(summary = "Create a member", description = "Create a new member. Similar to /register.")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Succesfully created member."),
            @APIResponse(responseCode = "409", description = "Email already in use")
    })
    public ApplicationUser createApplicationUser(ApplicationUser member) throws ConflictException {
        return applicationUserService.createApplicationUser(member);
    }

    @PUT
    @Path("/{id}")
    @Valid
    @Operation(summary = "Update a member", description = "Update a member.")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Succesfully updated member."),
            @APIResponse(responseCode = "409", description = "Email already in use")
    })
    public ApplicationUser updateApplicationUser(@PathParam("id") Long id, ApplicationUser member)
            throws ConflictException {
        member.setId(id);
        return applicationUserService.update(member);
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Delete a member", description = "Delete a member.")
    @APIResponses(value = {
            @APIResponse(responseCode = "204", description = "Succesfully deleted member.")
    })
    public void deleteApplicationUser(@PathParam("id") Long id) {
        applicationUserService.deleteApplicationUser(id);
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Get a member", description = "Get a member by id.")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Succesfully loaded member."),
            @APIResponse(responseCode = "204", description = "No member with this id.")
    })
    public ApplicationUser getApplicationUser(@PathParam("id") Long id) {
        return applicationUserService.find(id);
    }
}
