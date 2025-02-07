package cryptowallet;

import coinapi.client.CoinApiClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class CryptoWalletServiceTest {

    CoinApiClient coinApiClient = Mockito.mock(CoinApiClient.class);
    CryptoWalletService cryptoWalletService;

    @BeforeEach
    void setUp() {
        cryptoWalletService = new CryptoWalletService(coinApiClient);
    }

    @Test
    void depositMoneyInWalletNullCryptoWallet() {
        assertThrows(IllegalArgumentException.class, () -> cryptoWalletService.depositMoneyInWallet(10, null),
            "An IllegalArgumentException is expected when cryptoWallet is null reference!");
    }
}
