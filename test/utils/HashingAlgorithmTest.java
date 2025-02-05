package utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class HashingAlgorithmTest {

    @Test
    void testHashPasswordNullPassword() {
        assertThrows(IllegalArgumentException.class, () ->
                HashingAlgorithm.hashPassword(null),
            "An IllegalArgumentException is expected when password is null reference!");

    }

    @Test
    void testHashPasswordValidPassword() {
        String password = "password123";
        String hashedPassword = HashingAlgorithm.hashPassword(password);

        assertNotNull(hashedPassword);
        assertEquals(64, hashedPassword.length(), "hashedPassword must have length 64!");
    }

    @Test
    void testHashPasswordSamePassword() {
        String password1 = "passwordExample";
        String password2 = "passwordExample";

        String hashedPassword1 = HashingAlgorithm.hashPassword(password1);
        String hashedPassword2 = HashingAlgorithm.hashPassword(password2);

        assertEquals(hashedPassword1, hashedPassword2,
            "The hashing algorithm must be consistent!");
    }

    @Test
    void testHashPasswordDifferentPasswords() {

        String password1 = "passwordExample";
        String password2 = "differentPasswordExample";

        String hashedPassword1 = HashingAlgorithm.hashPassword(password1);
        String hashedPassword2 = HashingAlgorithm.hashPassword(password2);

        assertNotEquals(hashedPassword1, hashedPassword2,
            "The hashed passwords must be different when the original passwords are different");
    }
}
