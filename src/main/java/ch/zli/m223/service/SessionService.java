package ch.zli.m223.service;

import java.time.Duration;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import ch.zli.m223.domain.entity.ApplicationUser;
import io.smallrye.jwt.build.Jwt;

@ApplicationScoped
public class SessionService {
    @Inject
    ApplicationUserService userService;

    @Inject
    PasswordService passwordService;

    public String checkCredentials(ApplicationUser applicationUser) {
        Optional<ApplicationUser> user = userService.findByEmail(applicationUser.getEmail());
        if (user.isPresent() && user.get().getPassword().equals(passwordService.hashPassword(
                applicationUser.getPassword()))) {
            String role = user.get().getRole().name();
            String token = Jwt.issuer("https://coworking-space.example.com")
                    .upn(user.get().getEmail())
                    .groups(role)
                    .expiresIn(Duration.ofHours(24))
                    .sign();
            return token;
        } else {
            throw new SecurityException("Invalid login data");
        }
    }
}
