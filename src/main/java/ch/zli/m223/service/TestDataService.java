package ch.zli.m223.service;

import java.time.LocalDate;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import ch.zli.m223.domain.entity.ApplicationUser;
import ch.zli.m223.domain.entity.Booking;
import ch.zli.m223.domain.entity.BookingDuration;
import ch.zli.m223.domain.entity.Role;
import ch.zli.m223.domain.entity.State;
import io.quarkus.arc.profile.IfBuildProfile;
import io.quarkus.runtime.StartupEvent;

@IfBuildProfile("dev")
@ApplicationScoped
public class TestDataService {
    @Inject
    EntityManager entityManager;

    @Inject
    PasswordService passwordService;

    @Transactional
    void generateTestData(@Observes StartupEvent event) {
        entityManager.createNativeQuery("DELETE FROM booking CASCADE").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM applicationuser CASCADE").executeUpdate();

        String password1 = passwordService.hashPassword("password");
        ApplicationUser applicationUser1 = new ApplicationUser();
        applicationUser1.setFirstname("Max");
        applicationUser1.setLastname("Rüdiger");
        applicationUser1.setEmail("max.rüdiger@example.com");
        applicationUser1.setPassword(password1);
        applicationUser1.setRole(Role.ADMIN);
        entityManager.persist(applicationUser1);

        String password2 = passwordService.hashPassword("password");
        ApplicationUser applicationUser2 = new ApplicationUser();
        applicationUser2.setFirstname("Daniel");
        applicationUser2.setLastname("Müller");
        applicationUser2.setEmail("daniel.müller@example.com");
        applicationUser2.setPassword(password2);
        applicationUser2.setRole(Role.MEMBER);
        entityManager.persist(applicationUser2);

        String password3 = passwordService.hashPassword("password");
        ApplicationUser applicationUser3 = new ApplicationUser();
        applicationUser3.setFirstname("Max");
        applicationUser3.setLastname("Mustermann");
        applicationUser3.setEmail("max.mustermann@example.com");
        applicationUser3.setPassword(password3);
        applicationUser3.setRole(Role.MEMBER);
        entityManager.persist(applicationUser3);

        Booking booking1 = new Booking();
        booking1.setApplicationUser(applicationUser2);
        booking1.setBookingDuration(BookingDuration.FULLDAY);
        booking1.setDate(LocalDate.now());
        booking1.setState(State.PENDING);
        entityManager.persist(booking1);

        Booking booking2 = new Booking();
        booking2.setApplicationUser(applicationUser2);
        booking2.setBookingDuration(BookingDuration.MORNING);
        booking2.setDate(LocalDate.now().minusDays(1));
        booking2.setState(State.CANCELED);
        entityManager.persist(booking2);

        Booking booking3 = new Booking();
        booking3.setApplicationUser(applicationUser3);
        booking3.setBookingDuration(BookingDuration.NOON);
        booking3.setDate(LocalDate.now().minusDays(1));
        booking3.setState(State.ACCEPTED);
        entityManager.persist(booking3);
    }

}
