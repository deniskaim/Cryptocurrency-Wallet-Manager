package user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CryptoWalletTest {

    private CryptoWallet cryptoWallet;

    @BeforeEach
    void setUp() {
        cryptoWallet = new CryptoWallet();
    }

    @Test
    void testDepositMoneyNegativeAmount() {
        assertThrows(IllegalArgumentException.class, () -> cryptoWallet.depositMoney(-10),
            "An IllegalArgumentException is expected when the deposit amount is negative!");
    }

    @Test
    void testDepositMoneyZero() {
        assertThrows(IllegalArgumentException.class, () -> cryptoWallet.depositMoney(0),
            "An IllegalArgumentException is expected when the deposit amount is 0!");
    }

    @Test
    void testDepositMoney() {
        final double deposit = 10d;
        cryptoWallet.depositMoney(deposit);
        assertEquals(cryptoWallet.getBalance(), deposit, "Deposit money does not add the correct amount!");
    }

    @Test
    void testSetPurchasePriceOfAssetNullAsset() {
        assertThrows(IllegalArgumentException.class, () -> cryptoWallet.setPurchasePriceOfAsset(null, 10),
            "An IllegalArgumentException is expected when the assetID is null reference!");
    }

    @Test
    void testSetPurchasePriceOfAssetNegativePrice() {
        assertThrows(IllegalArgumentException.class, () -> cryptoWallet.setPurchasePriceOfAsset("random", -10),
            "An IllegalArgumentException is expected when the price is negative!");
    }

    @Test
    void testSetPurchasePriceOfAssetPriceIsZero() {
        assertThrows(IllegalArgumentException.class, () -> cryptoWallet.setPurchasePriceOfAsset("random", 0),
            "An IllegalArgumentException is expected when the price is zero!");
    }

    @Test
    void testSetPurchasePriceOfAsset() {
        cryptoWallet.setPurchasePriceOfAsset("randomAsset", 10);
        assertEquals(cryptoWallet.getCryptoPurchasePrices().get("randomAsset"), 10,
            "setPurchasePriceOfAsset does not set the correct value!");
    }
}
