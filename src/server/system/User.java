package server.system;

import java.io.Serializable;

// dto record
public record User(String username, String password, CryptoWallet balance) implements Serializable {

}
