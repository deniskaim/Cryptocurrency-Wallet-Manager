package server.system;

import java.io.Serializable;

public record User(String username, String password, CryptoWallet balance) implements Serializable {

}
