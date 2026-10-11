package com.clinic.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Minimal SHA-256 password hashing helper.
 * Good enough for a coursework demo; a production system should
 * use a salted algorithm such as bcrypt or Argon2 instead.
 */
public final class PasswordUtil {

    private PasswordUtil() {
    }

    public static String hash(String rawPassword) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(rawPassword.getBytes("UTF-8"));
            return Base64.getEncoder().encodeToString(hashBytes);
        } catch (NoSuchAlgorithmException | java.io.UnsupportedEncodingException e) {
            throw new RuntimeException("Unable to hash password", e);
        }
    }

    public static boolean matches(String rawPassword, String storedHash) {
        return hash(rawPassword).equals(storedHash);
    }
}
