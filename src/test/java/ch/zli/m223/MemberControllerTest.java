package ch.zli.m223;

import static io.restassured.RestAssured.given;

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
        testDataService.generateTestData(null);
    }

    @Test
    void testCreateEndpoint() {
        given().header("Content-type", "application/json").body(setUser()).when().post("/members").then()
                .statusCode(200);
        given().header("Content-type", "application/json").body(setUser()).when().post("/members").then()
                .statusCode(409);
    }

    @Test
    void testUpdateEndpoint() {
        ApplicationUser user = given().header("Content-type", "application/json").body(setUser()).when()
                .post("/members").as(ApplicationUser.class);
        given().header("Content-type", "application/json").body(setUser()).when().put("/members/" + user.getId()).then()
                .statusCode(200);
    }

    @Test
    void testDeleteEndpoint() {
        ApplicationUser user = given().header("Content-type", "application/json").body(setUser()).when()
                .post("/members").as(ApplicationUser.class);
        given().when().delete("/members/" + user.getId())
                .then()
                .statusCode(204);
    }

    @Test
    void testGetEndpoint() {
        ApplicationUser user = given().header("Content-type", "application/json").body(setUser()).when()
                .post("/members").as(ApplicationUser.class);
        given().when().get("/members/" + user.getId())
                .then()
                .statusCode(200);
    }

    @Test
    @TestSecurity(user = "daniel.müller@example.com", roles = { "MEMBER" })
    @JwtSecurity(claims = {
            @Claim(key = "upn", value = "daniel.müller@example.com")
    })
    void testChangeEmailEndpoint() {
        given().header("Content-type", "application/json").body(setUser()).when()
                .post("members/me/change/email").then().statusCode(200);
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