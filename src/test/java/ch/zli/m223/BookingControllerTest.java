package ch.zli.m223;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.junit.jupiter.api.BeforeEach;
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

        @Inject
        TestDataService testDataService;

        @BeforeEach
        void setup() {
                testDataService.generateTestData();
        }

        @Test
        void testCreateEndpointWithAvailableDateFullday() {
                Booking booking = new Booking();
                booking.setBookingDuration(BookingDuration.FULLDAY);
                booking.setDate(LocalDate.of(2022, 12, 4));
                given().header("Content-type", "application/json").body(booking).when().post("/bookings").then()
                                .statusCode(200);
        }

        @Test
        void testCreateEndpointWithAvailableDateMorning() {
                Booking booking = new Booking();
                booking.setBookingDuration(BookingDuration.MORNING);
                booking.setDate(LocalDate.of(2022, 12, 4));
                given().header("Content-type", "application/json").body(booking).when().post("/bookings").then()
                                .statusCode(200);
        }

        @Test
        void testCreateEndpointWithAvailableDateNoon() {
                Booking booking = new Booking();
                booking.setBookingDuration(BookingDuration.NOON);
                booking.setDate(LocalDate.of(2022, 12, 4));
                given().header("Content-type", "application/json").body(booking).when().post("/bookings").then()
                                .statusCode(200);
        }

        @Test
        void testCreateEndpointWithAvailableDuration() {
                Booking booking = new Booking();
                booking.setBookingDuration(BookingDuration.MORNING);
                booking.setDate(LocalDate.of(2022, 12, 3));
                given().header("Content-type", "application/json").body(booking).when().post("/bookings").then()
                                .statusCode(200);
        }

        @Test
        void testCreateEndpointWithUnavailableDateFullday() {
                Booking booking = new Booking();
                booking.setBookingDuration(BookingDuration.FULLDAY);
                booking.setDate(LocalDate.of(2022, 12, 2));
                given().header("Content-type", "application/json").body(booking).when().post("/bookings").then()
                                .statusCode(409);
        }

        @Test
        void testCreateEndpointWithUnavailableDateMorning() {
                Booking booking = new Booking();
                booking.setBookingDuration(BookingDuration.MORNING);
                booking.setDate(LocalDate.of(2022, 12, 2));
                given().header("Content-type", "application/json").body(booking).when().post("/bookings").then()
                                .statusCode(409);
        }

        @Test
        void testCreateEndpointWithUnavailableDateNoon() {
                Booking booking = new Booking();
                booking.setBookingDuration(BookingDuration.NOON);
                booking.setDate(LocalDate.of(2022, 12, 2));
                given().header("Content-type", "application/json").body(booking).when().post("/bookings").then()
                                .statusCode(409);
        }

        @Test
        void testCreateEndpointWithUnavailableDurationNoon() {
                Booking booking = new Booking();
                booking.setBookingDuration(BookingDuration.NOON);
                booking.setDate(LocalDate.of(2022, 12, 1));
                given().header("Content-type", "application/json").body(booking).when().post("/bookings").then()
                                .statusCode(409);
        }

        @Test
        void testCreateEndpointWithUnavailableDurationFullday() {
                Booking booking = new Booking();
                booking.setBookingDuration(BookingDuration.FULLDAY);
                booking.setDate(LocalDate.of(2022, 12, 1));
                given().header("Content-type", "application/json").body(booking).when().post("/bookings").then()
                                .statusCode(409);
        }

        @Test
        void testCreateEndpointOnCanceledBooking() {
                Booking booking = new Booking();
                booking.setBookingDuration(BookingDuration.MORNING);
                booking.setDate(LocalDate.of(2022, 12, 1));
                given().header("Content-type", "application/json").body(booking).when().post("/bookings").then()
                                .statusCode(200);
        }

        @Test
        void testCreateEndpointWithWrongData() {
                Booking booking = new Booking();
                given().header("Content-type", "application/json").body(booking).when().post("/bookings").then()
                                .statusCode(400);
        }

        @Test
        void testUpdateEndpoint() {
                Booking booking = new Booking();
                booking.setBookingDuration(BookingDuration.NOON);
                booking.setDate(LocalDate.of(2022, 9, 1));
                given().header("Content-type", "application/json").body(booking).when().put("/bookings/1").then()
                                .statusCode(200);
        }

        @TestSecurity(user = "daniel.müller@example.com", roles = { "MEMBER" })
        @JwtSecurity(claims = {
                        @Claim(key = "upn", value = "daniel.müller@example.com")
        })
        @Test
        void testUpdateEndpointInsufficientRights() {
                Booking booking = new Booking();
                given().header("Content-type", "application/json").body(booking).when().put("/bookings/1").then()
                                .statusCode(403);
        }

        @Test
        void testUpdateEndpointWithUnavailableDate() {
                Booking booking = new Booking();
                booking.setBookingDuration(BookingDuration.NOON);
                booking.setDate(LocalDate.of(2022, 12, 1));
                given().header("Content-type", "application/json").body(booking).when().put("/bookings/1").then()
                                .statusCode(409);
        }

        @Test
        void testUpdateEndpointWithCanceledBooking() {
                Booking booking = new Booking();
                Long id = createBooking();
                given().when().header("Content-type", "application/json").post("/bookings/cancel/" + id).then()
                                .statusCode(200);
                given().header("Content-type", "application/json").body(booking).when().put("/bookings/" + id).then()
                                .statusCode(400);
        }

        @Test
        void testDeleteEndpoint() {
                Long id = createBooking();
                given().when().delete("/bookings/" + id).then()
                                .statusCode(204);
                given().when().get("/bookings/" + id).then()
                                .statusCode(204);
        }

        @Test
        void testGetEndpoint() {
                Long id = createBooking();
                given().when().get("/bookings/" + id).then()
                                .statusCode(200).and().body(containsString("FULLDAY"));
        }

        @Test
        void testGetStateEndpoint() {
                Long id = createBooking();
                given().when().get("/bookings/state/" + id).then()
                                .statusCode(200).and().body(containsString("PENDING"));
        }

        @Test
        void testAcceptEndpoint() {
                Long id = createBooking();
                given().when().header("Content-type", "application/json").post("/bookings/accept/" + id).then()
                                .statusCode(200);
                given().when().get("/bookings/state/" + id).then()
                                .statusCode(200).and().body(containsString("ACCEPTED"));
        }

        @Test
        void testDenyEndpoint() {
                Long id = createBooking();
                given().when().header("Content-type", "application/json").post("/bookings/deny/" + id).then()
                                .statusCode(200);
                given().when().get("/bookings/state/" + id).then()
                                .statusCode(200).and().body(containsString("DENIED"));
        }

        @Test
        void testCancelEndpoint() {
                Long id = createBooking();
                given().when().header("Content-type", "application/json").post("/bookings/cancel/" + id).then()
                                .statusCode(200);
                given().when().get("/bookings/state/" + id).then()
                                .statusCode(200).and().body(containsString("CANCELED"));
        }

        @Test
        void testGetAvailableDatesEndpoint() {
                Map<LocalDate, BookingDuration> dates = new HashMap<>();
                for (int i = 0; i < 30; i++) {
                        dates.put(LocalDate.now().plusDays(i), BookingDuration.FULLDAY);
                }

                given().when().get("/bookings/available-dates").then()
                                .statusCode(200)
                                .body(containsString(dates.get(LocalDate.now()).toString()));
        }

        private Long createBooking() {
                Booking booking = new Booking();
                booking.setBookingDuration(BookingDuration.FULLDAY);
                booking.setDate(LocalDate.of(2022, 12, 4));
                return given().header("Content-type", "application/json").body(booking).when().post("/bookings")
                                .as(Booking.class).getId();
        }

}