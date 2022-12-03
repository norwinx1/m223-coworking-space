package ch.zli.m223.service;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import ch.zli.m223.domain.entity.ApplicationUser;
import ch.zli.m223.domain.model.Credentials;
import io.smallrye.jwt.build.Jwt;

@ApplicationScoped
public class SessionService {
    @Inject
    ApplicationUserService userService;

    @Inject
    PasswordService passwordService;

    public String checkCredentials(Credentials credentials) throws SecurityException {
        Optional<ApplicationUser> user = userService.findByEmail(credentials.getEmail());
        if (user.isPresent() && user.get().getPassword().equals(passwordService.hashPassword(
                credentials.getPassword()))) {
            String role = user.get().getRole().name();
            String token = Jwt.issuer("https://coworking-space.example.com")
                    .upn(user.get().getEmail())
                    .groups(new HashSet<>(Arrays.asList(role)))
                    .expiresIn(Duration.ofHours(24))
                    .sign();
            return token;
        } else {
            throw new SecurityException("Invalid login data");
        }
    }
}
