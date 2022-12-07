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
import io.quarkus.test.TestTransaction;
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
        @TestTransaction
        void testCreateEndpointWithAvailableDate() {
                Booking booking = new Booking();
                booking.setBookingDuration(BookingDuration.FULLDAY);
                booking.setDate(LocalDate.of(2022, 12, 1));
                given().header("Content-type", "application/json").body(booking).when().post("/bookings").then()
                                .statusCode(200);
        }

        @Test
        @Order(1)
        @TestTransaction
        void testCreateEndpointWithUnavailableDate() {
                Booking booking = new Booking();
                booking.setBookingDuration(BookingDuration.FULLDAY);
                booking.setDate(LocalDate.of(2022, 12, 1));
                given().header("Content-type", "application/json").body(booking).when().post("/bookings").then()
                                .statusCode(409);
        }

        @Test
        @Order(2)
        @TestTransaction
        void testCreateEndpointWithUnavailableDurationMorning() {
                Booking booking = new Booking();
                booking.setBookingDuration(BookingDuration.MORNING);
                booking.setDate(LocalDate.of(2022, 12, 1));
                given().header("Content-type", "application/json").body(booking).when().post("/bookings").then()
                                .statusCode(409);
        }

        @Test
        @Order(3)
        @TestTransaction
        void testCreateEndpointWithUnavailableDurationNoon() {
                Booking booking = new Booking();
                booking.setBookingDuration(BookingDuration.NOON);
                booking.setDate(LocalDate.of(2022, 12, 1));
                given().header("Content-type", "application/json").body(booking).when().post("/bookings").then()
                                .statusCode(409);
        }

        @Test
        @Order(4)
        @TestTransaction
        void testCreateEndpointWithUnavailableDurationFullday() {
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
        @TestTransaction
        void testCreateEndpointWithCanceledBooking() {
                // Set booking with canceled state
                Booking booking = new Booking();
                booking.setBookingDuration(BookingDuration.NOON);
                booking.setDate(LocalDate.of(2022, 11, 5));
                Long id = given().header("Content-type", "application/json").body(booking).when().post("/bookings")
                                .as(Booking.class).getId();
                given().header("Content-type", "application/json").when().post("/bookings/cancel/" + id).then()
                                .statusCode(200);

                // Create new booking on same date
                Booking booking2 = new Booking();
                booking2.setBookingDuration(BookingDuration.FULLDAY);
                booking2.setDate(LocalDate.of(2022, 11, 5));
                given().header("Content-type", "application/json").body(booking2).when().post("/bookings").then()
                                .statusCode(200);
        }

        @Test
        @Order(6)
        @TestTransaction
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
        @Order(7)
        @TestTransaction
        void testGetEndpointInsufficientRights() {
                Booking booking = new Booking();
                given().header("Content-type", "application/json").body(booking).when().get("/bookings/1").then()
                                .statusCode(403);
        }

        @Test
        @Order(8)
        @TestTransaction
        void testUpdateEndpoint() {
                Booking booking = new Booking();
                booking.setBookingDuration(BookingDuration.NOON);
                booking.setDate(LocalDate.of(2022, 9, 1));
                given().header("Content-type", "application/json").body(booking).when().post("/bookings/1").then()
                                .statusCode(200);
        }

        @Test
        @Order(9)
        @TestTransaction
        void testUpdateEndpointWithUnavailableDate() {
                Booking booking = new Booking();
                booking.setBookingDuration(BookingDuration.NOON);
                booking.setDate(LocalDate.of(2022, 12, 1));
                given().header("Content-type", "application/json").body(booking).when().post("/bookings").then()
                                .statusCode(409);
        }

        @Test
        @Order(10)
        @TestTransaction
        void testDeleteEndpointWithWrongId() {
                given().when().delete("/bookings/1").then()
                                .statusCode(204);
        }

        @Test
        @Order(11)
        @TestTransaction
        void testGetEndpoint() {
                given().when().get("/bookings/1").then()
                                .statusCode(200);
        }

        @Test
        @Order(12)
        @TestTransaction
        void testGetStateEndpointWithWrongId() {
                given().when().get("/bookings/state/1").then()
                                .statusCode(204);
        }

        @Test
        @Order(13)
        @TestTransaction
        void testCancelEndpoint() {
                given().when().header("Content-type", "application/json").post("/bookings/cancel/1").then()
                                .statusCode(200);
        }

        @Test
        @Order(14)
        @TestTransaction
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

}