package ch.zli.m223;

import static io.restassured.RestAssured.given;

import javax.inject.Inject;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ch.zli.m223.domain.entity.ApplicationUser;
import ch.zli.m223.domain.entity.Role;
import ch.zli.m223.domain.model.Credentials;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class SessionControllerTest {

    @Inject
    TestDataService testDataService;

    @BeforeEach
    void setup() {
        testDataService.generateTestData(null);
    }

    @Test
    void testLoginEndpointWithCorrectCredentials() {
        Credentials credentials = new Credentials();
        credentials.setEmail("max.rüdiger@example.com");
        credentials.setPassword("password");
        given().header("Content-type", "application/json").body(credentials).when().post("/login").then()
                .statusCode(200);
    }

    @Test
    void testLoginEndpointWithIncorrectCredentials() {
        Credentials credentials = new Credentials();
        credentials.setEmail("max.rüdiger@example.com");
        credentials.setPassword("passwrd");
        given().header("Content-type", "application/json").body(credentials).when().post("/login").then()
                .statusCode(401);
    }

    @Test
    void testLoginEndpointWithEmptyCredentials() {
        Credentials credentials = new Credentials();
        credentials.setEmail("max.rüdiger@example.com");
        credentials.setPassword("");
        given().header("Content-type", "application/json").body(credentials).when().post("/login").then()
                .statusCode(400);
    }

    @Test
    void testRegisterEndpointWithCorrectData() {
        ApplicationUser user = setUser();
        user.setEmail("test@test.com");
        given().header("Content-type", "application/json").body(user).when().post("/register").then()
                .statusCode(200);
    }

    @Test
    void testRegisterEndpointWithEmptyEmail() {
        ApplicationUser user = setUser();
        user.setEmail("");
        given().header("Content-type", "application/json").body(user).when().post("/register").then()
                .statusCode(400);
    }

    @Test
    void testRegisterEndpointWithNotUniqueEmail() {
        given().header("Content-type", "application/json").body(setUser()).when().post("/register").then()
                .statusCode(409);
    }

    private ApplicationUser setUser() {
        ApplicationUser applicationUser1 = new ApplicationUser();
        applicationUser1.setFirstname("Max");
        applicationUser1.setLastname("Rüdiger");
        applicationUser1.setEmail("max.rüdiger@example.com");
        applicationUser1.setPassword("password");
        applicationUser1.setRole(Role.ADMIN);
        return applicationUser1;
    }

}