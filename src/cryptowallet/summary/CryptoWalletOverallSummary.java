package cryptowallet.summary;

import java.util.Map;

public class CryptoWalletOverallSummary {

    private static final String OVERALL_PROFIT_MESSAGE = "Your investments have grown! Current profit: %f USD";
    private static final String OVERALL_LOSS_MESSAGE =
        "Current loss: %f USD. Investing always carries risks! Be patient!";
    private static final String OVERALL_NEUTRAL_MESSAGE = "No profit or loss at the moment. Your investments are safe!";

    private static final String PROFIT_MESSAGE = "Current profit: %f USD";
    private static final String LOSS_MESSAGE = "Current loss: %f USD";
    private static final String NEUTRAL_MESSAGE = "No profit or loss at the moment";

    private final double overallProfitLoss;
    private final Map<String, Double> assetsProfit;

    public CryptoWalletOverallSummary(double overallProfitLoss, Map<String, Double> assetsProfit) {
        this.overallProfitLoss = overallProfitLoss;
        this.assetsProfit = assetsProfit;
    }

    public double getOverallProfitLoss() {
        return overallProfitLoss;
    }

    public Map<String, Double> getAssetsProfit() {
        return assetsProfit;
    }

    @Override
    public String toString() {
        StringBuilder overallSummary = new StringBuilder(getCorrectResponse(overallProfitLoss));
        overallSummary.append(System.lineSeparator());

        for (Map.Entry<String, Double> entry : assetsProfit.entrySet()) {
            overallSummary.append(entry.getKey()).append(": ")
                .append(getMessageForAsset(entry.getValue()))
                .append(System.lineSeparator());
        }
        return overallSummary.toString();
    }

    private String getCorrectResponse(double overallProfitLoss) {
        if (Double.compare(overallProfitLoss, 0d) > 0) {
            return String.format(OVERALL_PROFIT_MESSAGE, overallProfitLoss);
        } else if (Double.compare(overallProfitLoss, 0d) < 0) {
            return String.format(OVERALL_LOSS_MESSAGE, -overallProfitLoss);
        } else {
            return OVERALL_NEUTRAL_MESSAGE;
        }
    }

    private String getMessageForAsset(double profitLoss) {
        if (Double.compare(profitLoss, 0d) > 0) {
            return String.format(PROFIT_MESSAGE, profitLoss);
        } else if (Double.compare(profitLoss, 0d) < 0) {
            return String.format(LOSS_MESSAGE, -profitLoss);
        } else {
            return NEUTRAL_MESSAGE;
        }
    }
}
