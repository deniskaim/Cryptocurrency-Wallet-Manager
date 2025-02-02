package server.system.user;

import java.io.Serializable;

public record AuthenticationData(String username, String password) implements Serializable {

    public static AuthenticationData of(String username, String password) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("username cannot be null reference or blank");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("password cannot be null reference or blank");
        }

        return new AuthenticationData(username, password);
    }
}
