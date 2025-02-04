package utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashingAlgorithm {

    private static final String HASHING_ALGORITHM = "SHA-256";
    private static final String BYTE_FORMAT = "%02x";

    public static String hashPassword(String password) {
        if (password == null) {
            throw new IllegalArgumentException("password cannot be null reference!");
        }

        try {
            MessageDigest messageDigest = MessageDigest.getInstance(HASHING_ALGORITHM);
            byte[] hashBytes = messageDigest.digest(password.getBytes());

            return buildHexStringFromBytes(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("An error occurred while trying to hash the password!", e);
        }
    }

    private static String buildHexStringFromBytes(byte[] hashBytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : hashBytes) {
            hexString.append(String.format(BYTE_FORMAT, b));
        }
        return hexString.toString();
    }
}
