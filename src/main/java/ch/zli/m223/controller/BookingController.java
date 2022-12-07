package ch.zli.m223.controller;

import java.time.LocalDate;
import java.util.Map;

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

import ch.zli.m223.domain.entity.ApplicationUser;
import ch.zli.m223.domain.entity.Booking;
import ch.zli.m223.domain.entity.BookingDuration;
import ch.zli.m223.domain.entity.Role;
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
    @Operation(summary = "Create a booking", description = "Create a booking. If date/duration is already booked the creation is automatically denied.")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Succesfully created booking. State is now pending."),
            @APIResponse(responseCode = "400", description = "Invalid data"),
            @APIResponse(responseCode = "401", description = "Unauthorized"),
            @APIResponse(responseCode = "409", description = "Date/duration is already booked. The booking has been automatically denied.")
    })
    public Booking createBooking(@Valid Booking booking) throws ConflictException {
        setUser(booking);
        return bookingsService.createBooking(booking);
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Update a booking", description = "Update a booking. If date/duration is already booked the creation is automatically denied.")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Succesfully updated booking."),
            @APIResponse(responseCode = "400", description = "This booking has already been canceled. No further changes are possible."),
            @APIResponse(responseCode = "400", description = "Invalid data"),
            @APIResponse(responseCode = "401", description = "Unauthorized"),
            @APIResponse(responseCode = "403", description = "Forbidden"),
            @APIResponse(responseCode = "409", description = "Date/duration is already booked. The booking has been automatically denied."),
    })
    @RolesAllowed({ "ADMIN" })
    public Booking updateBooking(@PathParam("id") Long id, @Valid Booking booking)
            throws ConflictException, BadRequestException {
        booking.setId(id);
        setUser(booking);
        return bookingsService.update(booking);
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Delete a booking", description = "Delete a booking.")
    @APIResponses(value = {
            @APIResponse(responseCode = "204", description = "Succesfully deleted booking."),
            @APIResponse(responseCode = "401", description = "Unauthorized"),
            @APIResponse(responseCode = "403", description = "Forbidden")
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
            @APIResponse(responseCode = "204", description = "No booking with this id."),
            @APIResponse(responseCode = "401", description = "Unauthorized"),
            @APIResponse(responseCode = "403", description = "Forbidden")
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
            @APIResponse(responseCode = "204", description = "No booking with this id."),
            @APIResponse(responseCode = "401", description = "Unauthorized"),
            @APIResponse(responseCode = "403", description = "Not a booking of the logged in user.")
    })
    public Response getState(@PathParam("id") Long id) throws ForbiddenException {
        Booking booking = bookingsService.find(id);
        if (booking != null) {
            if (isAdmin() || booking.getApplicationUser().equals(getUser())) {
                return Response.status(200).entity(booking.getState()).build();
            } else {
                throw new ForbiddenException("Forbidden");
            }
        } else {
            return Response.status(204).build();
        }
    }

    @POST
    @Path("/accept/{id}")
    @Operation(summary = "Accept a booking", description = "Accept a booking.")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Succesfully accepted the booking."),
            @APIResponse(responseCode = "400", description = "No booking with this id."),
            @APIResponse(responseCode = "400", description = "Booking has already been canceled."),
            @APIResponse(responseCode = "401", description = "Unauthorized"),
            @APIResponse(responseCode = "403", description = "Forbidden")
    })
    @RolesAllowed({ "ADMIN" })
    public Response acceptBooking(@PathParam("id") Long id) throws BadRequestException {
        bookingsService.setState(id, State.ACCEPTED);
        return Response.status(200).build();
    }

    @POST
    @Path("/deny/{id}")
    @Operation(summary = "Deny a booking", description = "Deny a booking.")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Succesfully denied the booking."),
            @APIResponse(responseCode = "400", description = "No booking with this id."),
            @APIResponse(responseCode = "400", description = "Booking has already been canceled."),
            @APIResponse(responseCode = "401", description = "Unauthorized"),
            @APIResponse(responseCode = "403", description = "Forbidden")
    })
    @RolesAllowed({ "ADMIN" })
    public Response denyBooking(@PathParam("id") Long id) throws BadRequestException {
        bookingsService.setState(id, State.DENIED);
        return Response.status(200).build();
    }

    @POST
    @Path("/cancel/{id}")
    @Operation(summary = "Cancel a booking", description = "Cancel a booking.")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Succesfully canceled the booking."),
            @APIResponse(responseCode = "400", description = "No booking with this id."),
            @APIResponse(responseCode = "400", description = "Booking has already been canceled."),
            @APIResponse(responseCode = "401", description = "Unauthorized"),
            @APIResponse(responseCode = "403", description = "Not a booking of the logged in user.")
    })
    public Response cancelBooking(@PathParam("id") Long id) throws BadRequestException, ForbiddenException {
        Booking booking = bookingsService.find(id);
        if (booking != null) {
            if (isAdmin() || booking.getApplicationUser().equals(getUser())) {
                bookingsService.setState(id, State.CANCELED);
                return Response.status(200).build();
            } else {
                throw new ForbiddenException("Forbidden");
            }
        } else {
            throw new BadRequestException("No booking with this id");
        }
    }

    @GET
    @Path("/available-dates")
    @Operation(summary = "Get available dates with durations", description = "Get available dates of the next 30 days with durations.")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Succesfully loaded all available dates of the next 30 days."),
            @APIResponse(responseCode = "204", description = "No available dates in the next 30 days."),
            @APIResponse(responseCode = "401", description = "Unauthorized")
    })
    public Map<LocalDate, BookingDuration> getAvailableDates() {
        return bookingsService.getAvailableDates();
    }

    private void setUser(Booking booking) {
        booking.setApplicationUser(getUser());
    }

    private ApplicationUser getUser() {
        return applicationUserService.findByEmail(jwt.getClaim("upn")).get();
    }

    private boolean isAdmin() {
        return jwt.getGroups().contains(Role.ADMIN.name());
    }
}
