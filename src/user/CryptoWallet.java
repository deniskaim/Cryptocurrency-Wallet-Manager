package user;

import exceptions.wallet.InsufficientFundsException;
import exceptions.wallet.MissingInWalletAssetException;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class CryptoWallet implements Serializable {

    @Serial
    private static final long serialVersionUID = 1234567891234567L;
    private static final String SUMMARY_MESSAGE = "Wallet Summary:" + System.lineSeparator();

    private double balance = 0;

    // cryptoAsset -> amount of crypto
    private final Map<String, Double> holdings = new HashMap<>();
    private final Map<String, Double> cryptoPurchasePrices = new HashMap<>();

    public void depositMoney(double amountToAdd) {
        if (Double.compare(amountToAdd, 0d) <= 0) {
            throw new IllegalArgumentException("Depositing an amount below 0.00 USD is not possible!");
        }
        balance += amountToAdd;
    }

    public void setPurchasePriceOfAsset(String assetID, double purchasePrice) {
        if (assetID == null) {
            throw new IllegalArgumentException("assetID cannot be null reference!");
        }
        if (Double.compare(purchasePrice, 0d) <= 0) {
            throw new IllegalArgumentException("A negative purchase price is not possible");
        }
        cryptoPurchasePrices.put(assetID, purchasePrice);
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

    public double getQuantityOfAsset(String assetID) throws MissingInWalletAssetException {
        if (assetID == null) {
            throw new IllegalArgumentException("assetID cannot be null reference!");
        }
        if (!holdings.containsKey(assetID)) {
            throw new MissingInWalletAssetException("There is no active investment in this crypto asset!");
        }
        return holdings.get(assetID);
    }

    public void removeAsset(String assetID) throws MissingInWalletAssetException {
        if (assetID == null) {
            throw new IllegalArgumentException("assetID cannot be null reference!");
        }
        if (!holdings.containsKey(assetID)) {
            throw new MissingInWalletAssetException("There is no active investment in this crypto asset!");
        }
        holdings.remove(assetID);
        cryptoPurchasePrices.remove(assetID);
    }

    public String getSummary() {
        StringBuilder summary = new StringBuilder(SUMMARY_MESSAGE);
        summary.append("Current balance = ").append(balance).append(" USD")
            .append(System.lineSeparator());
        if (holdings.isEmpty()) {
            summary.append("There are no crypto holdings.");
            return summary.toString();
        }

        for (var entry : holdings.entrySet()) {
            summary.append("CryptoCurrency: ").append(entry.getKey())
                .append(", Current Quantity: ").append(entry.getValue())
                .append(System.lineSeparator());
        }
        return summary.toString();
    }

    public double getInvestedMoney() {
        return holdings.keySet().stream()
            .mapToDouble(assetID -> holdings.get(assetID) * cryptoPurchasePrices.get(assetID))
            .sum();
    }

    public double getBalance() {
        return balance;
    }

    public Map<String, Double> getHoldings() {
        return holdings;
    }

    public boolean isAbleToSpend(double amount) {
        return Double.compare(balance, amount) >= 0;
    }

    public void withdrawMoney(double amount) throws InsufficientFundsException {
        if (!isAbleToSpend(amount)) {
            throw new InsufficientFundsException(
                "The balance in the CryptoWallet is lower than the desired amount to spend");
        }
        balance -= amount;
    }
}
