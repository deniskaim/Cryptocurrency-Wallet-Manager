package cryptowallet;

import java.util.HashMap;
import java.util.Map;

public record CryptoWalletSummary(double balance, Map<String, Double> holdings) {
    private static final String SUMMARY_MESSAGE = "Wallet Summary:" + System.lineSeparator();

    @Override
    public String toString() {
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

    static CryptoWalletSummary of(double balance, Map<String, Double> holdings) {
        return new CryptoWalletSummary(balance, new HashMap<>(holdings));
    }
}
