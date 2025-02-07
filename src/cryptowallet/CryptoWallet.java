package cryptowallet;

import exceptions.wallet.InsufficientFundsException;
import exceptions.wallet.MissingInWalletAssetException;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CryptoWallet implements Serializable {

    @Serial
    private static final long serialVersionUID = 1234567891234567L;

    private double balance = 0;

    // cryptoAssetID -> quantity of this crypto in active investments
    private final Map<String, Double> holdings = new HashMap<>();

    // cryptoAssetID -> history of active investments in this crypto
    private final Map<String, List<Investment>> investmentsHistory = new HashMap<>();

    public void depositMoney(double amountToAdd) {
        if (Double.compare(amountToAdd, 0d) <= 0) {
            throw new IllegalArgumentException("Depositing an amount below 0.00 USD is not possible!");
        }
        balance += amountToAdd;
    }

    public void addInvestment(String assetID, double boughtQuantity, double assetPrice) {
        validateAsset(assetID);
        validateQuantity(boughtQuantity);
        validatePrice(assetPrice);

        List<Investment> investmentsInCurrentAsset = getInvestmentsHistoryByAssetID(assetID);
        investmentsInCurrentAsset.add(Investment.of(assetID, boughtQuantity, assetPrice));

        holdings.put(assetID, holdings.getOrDefault(assetID, 0.0d) + boughtQuantity);
    }

    public double removeInvestment(String assetID) throws MissingInWalletAssetException {
        validateAsset(assetID);
        if (!holdings.containsKey(assetID)) {
            throw new MissingInWalletAssetException("There is no active investment in this crypto asset!");
        }

        double quantityInWallet = holdings.remove(assetID);
        investmentsHistory.remove(assetID);
        return quantityInWallet;
    }

    public CryptoWalletSummary getSummary() {
        return CryptoWalletSummary.of(balance, holdings);
    }

    public double getInvestedMoney() {
        return investmentsHistory.values().stream()
            .flatMap(List::stream)
            .mapToDouble(investment -> investment.purchasedQuantity() * investment.assetPrice())
            .sum();
    }

    public double getBalance() {
        return balance;
    }

    public Map<String, Double> getHoldings() {
        return holdings;
    }

    public void withdrawMoney(double amount) throws InsufficientFundsException {
        if (!isAbleToSpend(amount)) {
            throw new InsufficientFundsException(
                "The balance in the CryptoWallet is lower than the desired amount to spend!");
        }
        balance -= amount;
    }

    private boolean isAbleToSpend(double amount) {
        return Double.compare(balance, amount) >= 0;
    }

    private void validateAsset(String assetID) {
        if (assetID == null) {
            throw new IllegalArgumentException("assetID cannot be null reference!");
        }
    }

    private void validateQuantity(double quantity) {
        if (Double.compare(quantity, 0d) <= 0) {
            throw new IllegalArgumentException("Negative quantity is not possible!");
        }
    }

    private void validatePrice(double price) {
        if (Double.compare(price, 0d) <= 0) {
            throw new IllegalArgumentException("Negative price is not possible!");
        }
    }

    private List<Investment> getInvestmentsHistoryByAssetID(String assetID) {
        return investmentsHistory.computeIfAbsent(assetID, _ -> new ArrayList<>());
    }
}
