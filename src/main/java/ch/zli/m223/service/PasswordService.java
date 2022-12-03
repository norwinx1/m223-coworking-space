package ch.zli.m223.service;

import javax.enterprise.context.ApplicationScoped;

/*
 * Sligthly changed from source: https://medium.com/@kasunpdh/how-to-store-passwords-securely-with-pbkdf2-204487f14e84
 */
@ApplicationScoped
public class PasswordService {
    public String hashPassword(String password) {
        //TODO implement hashing
        return password;
    }
}
