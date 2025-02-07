package user;

import cryptowallet.CryptoWallet;

import java.io.Serial;
import java.io.Serializable;

public record User(AuthenticationData authenticationData, CryptoWallet cryptoWallet) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1234567891234567L;

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
