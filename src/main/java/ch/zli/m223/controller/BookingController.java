package ch.zli.m223.controller;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import antlr.collections.List;
import ch.zli.m223.domain.entity.ApplicationUser;
import ch.zli.m223.domain.entity.Booking;
import ch.zli.m223.domain.entity.BookingDuration;
import ch.zli.m223.domain.entity.State;
import ch.zli.m223.domain.exception.ConflictException;
import ch.zli.m223.service.ApplicationUserService;
import ch.zli.m223.service.BookingService;

@Path("/bookings")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Bookings", description = "Handling of bookings")
@RolesAllowed({ "ADMIN", "MEMBER" })
public class BookingController {
    @Inject
    JsonWebToken jwt;

    @Inject
    BookingService bookingsService;

    @Inject
    ApplicationUserService applicationUserService;

    @POST
    @Valid
    @Operation(summary = "Create a booking", description = "Create a booking. If date/state is already booked the creation is automatically denied.")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Succesfully created booking. State is now pending."),
            @APIResponse(responseCode = "409", description = "Date/duration is already booked. The booking has been automatically denied.")
    })
    public Booking createBooking(Booking booking) throws ConflictException {
        setUser(booking);
        return bookingsService.createBooking(booking);
    }

    @PUT
    @Path("/{id}")
    @Valid
    @Operation(summary = "Update a booking", description = "Update a booking. If date/state is already booked the creation is automatically denied.")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Succesfully updated booking."),
            @APIResponse(responseCode = "400", description = "This booking has already been canceled. No further changes are possible."),
            @APIResponse(responseCode = "409", description = "Date/duration is already booked. The booking has been automatically denied.")
    })
    @RolesAllowed({ "ADMIN" })
    public Booking updateBooking(@PathParam("id") Long id, Booking booking) throws ConflictException {
        booking.setId(id);
        setUser(booking);
        return bookingsService.update(booking);
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Delete a booking", description = "Delete a booking.")
    @APIResponses(value = {
            @APIResponse(responseCode = "204", description = "Succesfully deleted booking.")
    })
    @RolesAllowed({ "ADMIN" })
    public void deleteBooking(@PathParam("id") Long id) {
        bookingsService.deleteBooking(id);
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Get a booking", description = "Get a booking.")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Succesfully loaded booking."),
            @APIResponse(responseCode = "204", description = "No booking with this id.")
    })
    @RolesAllowed({ "ADMIN" })
    public Booking getBooking(@PathParam("id") Long id) {
        return bookingsService.find(id);
    }

    @GET
    @Path("/state/{id}")
    @Operation(summary = "Get the state of a booking", description = "Get the state of a booking.")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Succesfully loaded the state."),
            @APIResponse(responseCode = "403", description = "Not a booking of the logged in user."),
            @APIResponse(responseCode = "204", description = "No booking with this id.")
    })
    public State getState(@PathParam("id") Long id) {
        Booking booking = bookingsService.find(id);
        if (booking != null && booking.getApplicationUser().getEmail().equals(jwt.getClaim("upn"))) {
            return booking.getState();
        } else {
            throw new ForbiddenException("Insufficient rights");
        }
    }

    @POST
    @Path("/accept/{id}")
    @Operation(summary = "Accept a booking", description = "Accept a booking.")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Succesfully accepted the booking."),
            @APIResponse(responseCode = "204", description = "No booking with this id."),
            @APIResponse(responseCode = "400", description = "Booking has already been canceled.")
    })
    @RolesAllowed({ "ADMIN" })
    public Response acceptBooking(@PathParam("id") Long id) throws BadRequestException {
        Booking booking = bookingsService.find(id);
        if (booking != null) {
            if (booking.getState().equals(State.CANCELED)) {
                throw new BadRequestException("Booking has already been canceled");
            }
            booking.setState(State.ACCEPTED);
            return Response.status(200).build();
        } else {
            return Response.status(204).build();
        }
    }

    @POST
    @Path("/deny/{id}")
    @Operation(summary = "Accept a booking", description = "Accept a booking.")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Succesfully accepted the booking."),
            @APIResponse(responseCode = "204", description = "No booking with this id."),
            @APIResponse(responseCode = "400", description = "Booking has already been canceled.")
    })
    @RolesAllowed({ "ADMIN" })
    public Response denyBooking(@PathParam("id") Long id) throws BadRequestException {
        Booking booking = bookingsService.find(id);
        if (booking != null) {
            if (booking.getState().equals(State.CANCELED)) {
                throw new BadRequestException("Booking has already been canceled");
            }
            booking.setState(State.DENIED);
            return Response.status(200).build();
        } else {
            return Response.status(204).build();
        }
    }

    @POST
    @Path("/cancel/{id}")
    @Operation(summary = "Accept a booking", description = "Accept a booking.")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Succesfully accepted the booking."),
            @APIResponse(responseCode = "204", description = "No booking with this id."),
            @APIResponse(responseCode = "400", description = "Booking has already been canceled.")
    })
    public Response cancelBooking(@PathParam("id") Long id) throws BadRequestException {
        Booking booking = bookingsService.find(id);
        if (booking != null) {
            if (booking.getState().equals(State.CANCELED)) {
                throw new BadRequestException("Booking has already been canceled");
            }
            booking.setState(State.CANCELED);
            return Response.status(200).build();
        } else {
            return Response.status(204).build();
        }
    }

    @GET
    @Path("/available-dates")
    @Operation(summary = "Get available dates/durations", description = "Get available dates/durations.")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Succesfully loaded all available dates of the next 30 days."),
            @APIResponse(responseCode = "204", description = "No available dates in the next 30 days.")
    })
    public Map<LocalDate, BookingDuration> getAvailableDates() {
        return bookingsService.getAvailableDates();
    }

    private void setUser(Booking booking) {
        Optional<ApplicationUser> applicationUser = applicationUserService.findByEmail(jwt.getClaim("upn"));
        booking.setApplicationUser(applicationUser.get());
    }
}
