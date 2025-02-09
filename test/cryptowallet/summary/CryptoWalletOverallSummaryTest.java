package cryptowallet.summary;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CryptoWalletOverallSummaryTest {

    private Map<String, Double> assetsProfit;

    @BeforeEach
    void setUp() {
        assetsProfit = new HashMap<>();
    }

    @Test
    void testToStringFormatProfit() {
        assetsProfit.put("BTC", 500.0);
        assetsProfit.put("ETH", -200.0);
        CryptoWalletOverallSummary summary = new CryptoWalletOverallSummary(300.0, assetsProfit);

        String expected = "Your investments have grown! Current profit: 300.000000 USD" +
            System.lineSeparator() + "BTC: Current profit: 500.000000 USD" +
            System.lineSeparator() + "ETH: Current loss: 200.000000 USD" +
            System.lineSeparator();

        assertEquals(expected, summary.toString(), "Generated summary string does not match");
    }

    @Test
    void testToStringFormatLoss() {
        assetsProfit.put("BTC", -500.0);
        assetsProfit.put("ETH", 200.0);
        CryptoWalletOverallSummary summary = new CryptoWalletOverallSummary(-300.0, assetsProfit);

        String expected = "Current loss: 300.000000 USD. Investing always carries risks! Be patient!" +
            System.lineSeparator() + "BTC: Current loss: 500.000000 USD" +
            System.lineSeparator() + "ETH: Current profit: 200.000000 USD" +
            System.lineSeparator();

        assertEquals(expected, summary.toString(), "Generated summary string does not match");
    }

    @Test
    void testToStringFormatNegative() {
        assetsProfit.put("BTC", -500.0);
        assetsProfit.put("ETH", 500.0);
        CryptoWalletOverallSummary summary = new CryptoWalletOverallSummary(0, assetsProfit);

        String expected = "No profit or loss at the moment. Your investments are safe!" +
            System.lineSeparator() + "BTC: Current loss: 500.000000 USD" +
            System.lineSeparator() + "ETH: Current profit: 500.000000 USD" +
            System.lineSeparator();

        assertEquals(expected, summary.toString(), "Generated summary string does not match");
    }
}
