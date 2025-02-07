package cryptowallet;

import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CryptoWalletSummaryTest {

    @Test
    void testToStringWithHoldings() {
        Map<String, Double> holdings = new LinkedHashMap<>();
        holdings.put("ETH", 10.0);
        holdings.put("BTC", 1.5);

        CryptoWalletSummary summary = new CryptoWalletSummary(1000.0, holdings);

        String expectedResult = "Wallet Summary:" + System.lineSeparator()
            + "Current balance = 1000.0 USD" + System.lineSeparator()
            + "CryptoCurrency: ETH, Current Quantity: 10.0" + System.lineSeparator()
            + "CryptoCurrency: BTC, Current Quantity: 1.5" + System.lineSeparator();

        assertEquals(expectedResult.strip(), summary.toString().strip(),
            "That's not the correct representation of the wallet summary");
    }

    @Test
    void testToStringWithoutHoldings() {
        Map<String, Double> holdings = new LinkedHashMap<>();

        CryptoWalletSummary summary = new CryptoWalletSummary(500.0, holdings);

        String expectedResult = "Wallet Summary:" + System.lineSeparator()
            + "Current balance = 500.0 USD" + System.lineSeparator()
            + "There are no crypto holdings." + System.lineSeparator();

        assertEquals(expectedResult.strip(), summary.toString().strip(),
            "That's not the correct representation of the wallet summary");
    }
}
