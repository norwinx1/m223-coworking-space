package ch.zli.m223.service;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.jwt.JsonWebToken;

import ch.zli.m223.domain.entity.ApplicationUser;
import ch.zli.m223.domain.entity.Role;
import ch.zli.m223.domain.model.Credentials;
import io.smallrye.jwt.build.Jwt;

@ApplicationScoped
public class SessionService {
    @Inject
    ApplicationUserService userService;

    @Inject
    PasswordService passwordService;

    @Inject
    JsonWebToken jwt;

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

    public ApplicationUser getUser() {
        return userService.findByEmail(jwt.getClaim("upn")).get();
    }

    public boolean isAdmin() {
        return jwt.getGroups().contains(Role.ADMIN.name());
    }
}
