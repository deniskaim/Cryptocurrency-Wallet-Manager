package server.system.user;

import java.io.Serializable;

public class CryptoWallet implements Serializable {

    private double balance = 0;
    private static final String CURRENCY = "USD";

    public void depositMoney(double amountToAdd) {
        balance += amountToAdd;
    }
}
