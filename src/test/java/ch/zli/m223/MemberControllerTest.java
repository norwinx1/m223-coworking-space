package ch.zli.m223;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;

import javax.inject.Inject;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ch.zli.m223.domain.entity.ApplicationUser;
import ch.zli.m223.domain.entity.Role;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.quarkus.test.security.jwt.Claim;
import io.quarkus.test.security.jwt.JwtSecurity;

@QuarkusTest
@TestSecurity(user = "max.rüdiger@example.com", roles = { "ADMIN" })
@JwtSecurity(claims = {
                @Claim(key = "upn", value = "max.rüdiger@example.com")
})
public class MemberControllerTest {

        @Inject
        TestDataService testDataService;

        @BeforeEach
        void setup() {
                testDataService.generateTestData();
        }

        @Test
        void testCreateEndpoint() {
                given().header("Content-type", "application/json").body(setUser()).when().post("/members").then()
                                .statusCode(200);
                given().when().get("/members/" + 4)
                                .then()
                                .statusCode(200)
                                .and()
                                .body(containsString("max.henry@example.com"));
        }

        @Test
        void testCreateNotUniqueEndpoint() {
                ApplicationUser applicationUser = new ApplicationUser();
                applicationUser.setFirstname("Max");
                applicationUser.setLastname("Rüdiger");
                applicationUser.setEmail("max.rüdiger@example.com");
                applicationUser.setPassword("password");
                applicationUser.setRole(Role.ADMIN);
                given().header("Content-type", "application/json").body(applicationUser).when().post("/members").then()
                                .statusCode(409);
        }

        @Test
        void testUpdateEndpoint() {
                given().header("Content-type", "application/json").body(setUser()).when().put("/members/" + 2).then()
                                .statusCode(200);
                given().when().get("/members/" + 2)
                                .then()
                                .statusCode(200)
                                .and()
                                .body(containsString("max.henry@example.com"));
        }

        @Test
        void testDeleteEndpoint() {
                given().when().delete("/members/" + 2)
                                .then()
                                .statusCode(204);
                given().when().get("/members/" + 2)
                                .then()
                                .statusCode(204);
        }

        @Test
        void testGetEndpoint() {
                given().when().get("/members/" + 1)
                                .then()
                                .statusCode(200)
                                .and()
                                .body(containsString("max.rüdiger@example.com"));
        }

        @Test
        @TestSecurity(user = "daniel.müller@example.com", roles = { "MEMBER" })
        @JwtSecurity(claims = {
                        @Claim(key = "upn", value = "daniel.müller@example.com")
        })
        void testChangeEmailEndpoint() {
                given().header("Content-type", "application/json").body("test@test.com").when()
                                .post("members/me/change/email").then().statusCode(200).body(containsString(
                                                "test@test.com"));
        }

        @Test
        @TestSecurity(user = "daniel.müller@example.com", roles = { "MEMBER" })
        @JwtSecurity(claims = {
                        @Claim(key = "upn", value = "daniel.müller@example.com")
        })
        void testChangeEmailEndpointWithBlankEmail() {
                given().header("Content-type", "application/json").body("").when()
                                .post("members/me/change/email").then().statusCode(400);
        }

        private ApplicationUser setUser() {
                ApplicationUser applicationUser1 = new ApplicationUser();
                applicationUser1.setFirstname("Max");
                applicationUser1.setLastname("Henry");
                applicationUser1.setEmail("max.henry@example.com");
                applicationUser1.setPassword("password");
                applicationUser1.setRole(Role.ADMIN);
                return applicationUser1;
        }

}