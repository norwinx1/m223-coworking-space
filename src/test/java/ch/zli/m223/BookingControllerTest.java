package ch.zli.m223;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import ch.zli.m223.domain.entity.Booking;
import ch.zli.m223.domain.entity.BookingDuration;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.quarkus.test.security.jwt.Claim;
import io.quarkus.test.security.jwt.JwtSecurity;

@QuarkusTest
@TestSecurity(user = "max.rüdiger@example.com", roles = { "ADMIN" })
@JwtSecurity(claims = {
        @Claim(key = "upn", value = "max.rüdiger@example.com")
})
public class BookingControllerTest {

    @Test
    @Order(0)
    void testCreateEndpointWithAvailableDate() {
        Booking booking = new Booking();
        booking.setBookingDuration(BookingDuration.FULLDAY);
        booking.setDate(LocalDate.of(2022, 12, 1));
        given().header("Content-type", "application/json").body(booking).when().post("/bookings").then()
                .statusCode(200);
    }

    @Test
    @Order(1)
    void testCreateEndpointWithUnavailableDate() {
        Booking booking = new Booking();
        booking.setBookingDuration(BookingDuration.FULLDAY);
        booking.setDate(LocalDate.of(2022, 12, 1));
        given().header("Content-type", "application/json").body(booking).when().post("/bookings").then()
                .statusCode(409);
    }

    @Test
    @Order(2)
    void testCreateEndpointWithUnavailableDurationMorning() {
        Booking booking = new Booking();
        booking.setBookingDuration(BookingDuration.MORNING);
        booking.setDate(LocalDate.of(2022, 12, 1));
        given().header("Content-type", "application/json").body(booking).when().post("/bookings").then()
                .statusCode(409);
    }

    @Test
    @Order(3)
    void testCreateEndpointWithUnavailableDurationNoon() {
        Booking booking = new Booking();
        booking.setBookingDuration(BookingDuration.NOON);
        booking.setDate(LocalDate.of(2022, 12, 1));
        given().header("Content-type", "application/json").body(booking).when().post("/bookings").then()
                .statusCode(409);
    }

    @Test
    @Order(4)
    void testCreateEndpointWithUnavailableDuration() {
        Booking booking = new Booking();
        booking.setBookingDuration(BookingDuration.NOON);
        booking.setDate(LocalDate.of(2022, 11, 1));
        given().header("Content-type", "application/json").body(booking).when().post("/bookings").then()
                .statusCode(200);

        Booking booking2 = new Booking();
        booking2.setBookingDuration(BookingDuration.FULLDAY);
        booking2.setDate(LocalDate.of(2022, 11, 1));
        given().header("Content-type", "application/json").body(booking2).when().post("/bookings").then()
                .statusCode(409);
    }

    @Test
    @Order(5)
    void testCreateEndpointWithWrongData() {
        Booking booking = new Booking();
        given().header("Content-type", "application/json").body(booking).when().post("/bookings").then()
                .statusCode(400);
    }

    @TestSecurity(user = "daniel.müller@example.com", roles = { "MEMBER" })
    @JwtSecurity(claims = {
            @Claim(key = "upn", value = "daniel.müller@example.com")
    })
    @Test
    @Order(6)
    void testEndpointInsufficientRights() {
        Booking booking = new Booking();
        given().header("Content-type", "application/json").body(booking).when().get("/bookings/1").then()
                .statusCode(403);
    }

    @Test
    @Order(7)
    void testUpdateEndpointWithUnavailableDate() {
        Booking booking = new Booking();
        booking.setBookingDuration(BookingDuration.NOON);
        booking.setDate(LocalDate.of(2022, 12, 1));
        given().header("Content-type", "application/json").body(booking).when().post("/bookings").then()
                .statusCode(409);
    }

    @Test
    @Order(7)
    void testUpdateEndpointWithoutUser() {
        Booking booking = new Booking();
        booking.setBookingDuration(BookingDuration.NOON);
        booking.setDate(LocalDate.of(2022, 9, 1));
        given().header("Content-type", "application/json").body(booking).when().put("/bookings/1").then()
                .statusCode(400);
    }

    @Test
    @Order(8)
    void testDeleteEndpoint() {
        given().when().delete("/bookings/1").then()
                .statusCode(204);
    }

    @Test
    @Order(9)
    void testGetEndpoint() {
        given().when().get("/bookings/1").then()
                .statusCode(204);
    }

    @Test
    @Order(10)
    void testGetStateEndpoint() {
        given().when().get("/bookings/state/1").then()
                .statusCode(204);
    }

    @Test
    @Order(11)
    void testGetAvailableDatesEndpoint() {
        Map<LocalDate, BookingDuration> dates = new HashMap<>();
        for (int i = 0; i < 30; i++) {
            dates.put(LocalDate.now().plusDays(i), BookingDuration.FULLDAY);
        }
        // See also TestDataService
        dates.remove(LocalDate.now());
        dates.remove(LocalDate.now().plusDays(5));

        given().when().get("/bookings/available-dates").then()
                .statusCode(200)
                .body(containsString(dates.get(LocalDate.now().plusDays(1)).toString()));
    }

    // TODO Add last endpoints

}