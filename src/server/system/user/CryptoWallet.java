package server.system.user;

import exceptions.InsufficientFundsException;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class CryptoWallet implements Serializable {

    @Serial
    private static final long serialVersionUID = 1234567891234567L;

    private double balance = 0;

    // cryptoCode -> amount of crypto
    private final Map<String, Double> holdings = new HashMap<>();

    public void depositMoney(double amountToAdd) {
        if (Double.compare(amountToAdd, 0d) <= 0) {
            throw new IllegalArgumentException("Depositing an amount below 0.00 USD is not possible!");
        }
        balance += amountToAdd;
    }

    public void addQuantityToWallet(String assetID, double boughtQuantity) {
        if (assetID == null) {
            throw new IllegalArgumentException("assetID cannot be null reference!");
        }
        if (Double.compare(boughtQuantity, 0d) <= 0) {
            throw new IllegalArgumentException("Buying a negative quantity is not possible");
        }

        holdings.put(assetID, holdings.getOrDefault(assetID, 0.0d) + boughtQuantity);
    }

    public double getBalance() {
        return balance;
    }

    public boolean isAbleToSpend(double amount) {
        return Double.compare(balance, amount) >= 0;
    }

}
