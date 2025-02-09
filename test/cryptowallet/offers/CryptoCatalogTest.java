package cryptowallet.offers;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CryptoCatalogTest {

    @Test
    void testToStringWhenListingOfferings() {
        Offering offering1 = Offering.of("BTC", "Bitcoin",96000.0);
        Offering offering2 = Offering.of("ETH", "Ethereum", 3200.0);

        CryptoCatalog cryptoCatalog = new CryptoCatalog(List.of(offering1, offering2));
        String expectedResult = "Available Cryptocurrencies:" + System.lineSeparator()
            + "BTC, Bitcoin: 96000.0 USD" + System.lineSeparator() + "ETH, Ethereum: 3200.0 USD";

        String result = cryptoCatalog.toString();
        assertEquals(expectedResult, result, "CryptoCatalog does not represent correctly the available assets!");
    }
}
