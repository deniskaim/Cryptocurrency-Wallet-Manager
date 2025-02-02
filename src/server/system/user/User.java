package server.system.user;

import java.io.Serializable;

public record User(AuthenticationData authenticationData, CryptoWallet cryptoWallet) implements Serializable {

    public static User of(AuthenticationData authenticationData, CryptoWallet cryptoWallet) {
        if (authenticationData == null) {
            throw new IllegalArgumentException("authenticationData cannot be null reference!");
        }
        if (cryptoWallet == null) {
            throw new IllegalArgumentException("cryptoWallet cannot be null reference!");
        }

        return new User(authenticationData, cryptoWallet);
    }
}
