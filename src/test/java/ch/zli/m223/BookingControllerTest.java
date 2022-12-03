package ch.zli.m223;

import static io.restassured.RestAssured.given;

import java.time.LocalDate;

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

}