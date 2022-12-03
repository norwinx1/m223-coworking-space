package ch.zli.m223.controller;

import java.util.Optional;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import ch.zli.m223.domain.entity.ApplicationUser;
import ch.zli.m223.domain.entity.Booking;
import ch.zli.m223.domain.exception.ConflictException;
import ch.zli.m223.service.ApplicationUserService;
import ch.zli.m223.service.BookingsService;

@Path("/bookings")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Bookings", description = "Handling of bookings")
@RolesAllowed({ "ADMIN", "MEMBER" })
public class BookingsController {
    @Inject
    JsonWebToken jwt;

    @Inject
    BookingsService bookingsService;

    @Inject
    ApplicationUserService applicationUserService;

    @POST
    @Valid
    @Operation(summary = "Create a booking", description = "Create a booking. If date/state is already booked the creation is automatically denied.")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Succesfully created booking. State is now pending."),
            @APIResponse(responseCode = "409", description = "Date/state is already booked. The creation has been automatically denied.")
    })
    public Booking createBooking(Booking booking) throws ConflictException {
        Optional<ApplicationUser> applicationUser = applicationUserService.findByEmail(jwt.getClaim("upn"));
        booking.setApplicationUser(applicationUser.get());
        return bookingsService.createBooking(booking);
    }
}
