package ch.zli.m223.service;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.enterprise.context.ApplicationScoped;

import org.apache.commons.codec.binary.Hex;

/*
 * Sligthly changed from source: https://medium.com/@kasunpdh/how-to-store-passwords-securely-with-pbkdf2-204487f14e84
 */
@ApplicationScoped
public class PasswordService {
    public String hashPassword(String password) {
        String pepper = "0923lkynsdgl0293m,.äpo7blksd";
        int iterations = 10000;
        int keyLength = 512;
        char[] passwordChars = password.toCharArray();
        byte[] saltBytes = pepper.getBytes();

        byte[] hashedBytes = hash(passwordChars, saltBytes, iterations, keyLength);
        return Hex.encodeHexString(hashedBytes);
    }

    public static byte[] hash(final char[] password, final byte[] salt, final int iterations,
            final int keyLength) {
        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
            PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, keyLength);
            SecretKey key = skf.generateSecret(spec);
            byte[] res = key.getEncoded();
            return res;
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }
}
