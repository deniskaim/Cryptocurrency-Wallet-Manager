package server.system.user;

import java.io.Serial;
import java.io.Serializable;

public class CryptoWallet implements Serializable {

    @Serial
    private static final long serialVersionUID = 1234567891234567L;

    private double balance = 0;
    private static final String CURRENCY = "USD";

    public void depositMoney(double amountToAdd) {
        if (Double.compare(amountToAdd, 0d) <= 0) {
            throw new IllegalArgumentException("The amount in the deposit-money command cannot be below 0.00 USD");
        }
        balance += amountToAdd;
    }

    public double getBalance() {
        return balance;
    }


}
