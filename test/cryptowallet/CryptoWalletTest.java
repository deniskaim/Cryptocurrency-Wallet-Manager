package cryptowallet;

import exceptions.wallet.InsufficientFundsException;
import exceptions.wallet.MissingInWalletAssetException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    void testWithdrawMoneyNegativeAmount() {
        assertThrows(IllegalArgumentException.class, () -> cryptoWallet.withdrawMoney(-10),
            "An IllegalArgumentException is expected when the withdraw amount is negative!");
    }

    @Test
    void testWithdrawMoneyZero() {
        assertThrows(IllegalArgumentException.class, () -> cryptoWallet.withdrawMoney(0),
            "An IllegalArgumentException is expected when the withdraw amount is negative!");
    }

    @Test
    void testWithdrawMoneyInsufficientFunds() {
        CryptoWallet cryptoWalletTest = new CryptoWallet(10, new HashMap<>(), new HashMap<>());
        assertThrows(InsufficientFundsException.class, () -> cryptoWalletTest.withdrawMoney(20),
            "An InsufficientFundsException is expected when the withdraw amount is bigger than the balance!");
    }

    @Test
    void testWithdrawMoney() throws InsufficientFundsException {
        CryptoWallet cryptoWalletTest = new CryptoWallet(100, new HashMap<>(), new HashMap<>());
        cryptoWalletTest.withdrawMoney(20);

        final double expectedBalance = 80d;
        double resultBalance = cryptoWalletTest.getBalance();
        assertEquals(expectedBalance, resultBalance, "withdrawMoney() does not work correctly!");
    }

    @Test
    void testAddInvestmentNullAssetID() {
        assertThrows(IllegalArgumentException.class, () -> cryptoWallet.addInvestment(null, 1, 1),
            "An IllegalArgumentException is expected when assetID is null reference!");
    }

    @Test
    void testAddInvestmentNegativeQuantity() {
        assertThrows(IllegalArgumentException.class, () -> cryptoWallet.addInvestment("assetID", -1, 1),
            "An IllegalArgumentException is expected when the quantity is negative!");

    }

    @Test
    void testAddInvestmentNegativePrice() {
        assertThrows(IllegalArgumentException.class, () -> cryptoWallet.addInvestment("assetID", 1, -1),
            "An IllegalArgumentException is expected when the asset price is negative!");
    }

    @Test
    void testAddInvestment() {
        cryptoWallet.addInvestment("assetID", 1, 100);
        cryptoWallet.addInvestment("assetID", 0.5, 95);

        List<Investment> investmentsInCrypto = cryptoWallet.getInvestmentsHistory().get("assetID");
        double quantity = cryptoWallet.getHoldings().get("assetID");

        final int expectedSize = 2;
        assertEquals(expectedSize, investmentsInCrypto.size(),
            "addInvestment() does not add the investments in the history correctly!");

        final double expectedQuantity = 1.5;
        assertEquals(expectedQuantity, quantity, "addInvestment() does not calculate the quantity correctly!");
    }

    @Test
    void testRemoveInvestmentNullAssetID() {
        assertThrows(IllegalArgumentException.class, () -> cryptoWallet.removeInvestment(null),
            "An IllegalArgumentException is expected when assetID is null reference!");
    }

    @Test
    void testRemoveInvestmentShouldThrowMissingInWalletAssetException() {
        assertThrows(MissingInWalletAssetException.class, () -> cryptoWallet.removeInvestment("missingAsset"),
            "An MissingInWalletAssetException is expected when assetID does not exist in the history!");
    }

    @Test
    void testRemoveInvestment() throws MissingInWalletAssetException {

        CryptoWallet cryptoWalletTest = new CryptoWallet(0, getHoldings(), getInvestmentsHistory());

        final double expectedQuantity = 1.5;
        double resultQuantity = cryptoWalletTest.removeInvestment("assetID");

        assertEquals(expectedQuantity, resultQuantity, "removeInvestment() does not return the correct quantity!");
        assertTrue(cryptoWalletTest.getHoldings().isEmpty(), "holdings must be empty!");
    }

    @Test
    void testGetInvestedMoney() {
        CryptoWallet cryptoWalletTest = new CryptoWallet(0, getHoldings(), getInvestmentsHistory());

        final double expectedMoney = 100;
        double result = cryptoWalletTest.getInvestedMoney();
        assertEquals(expectedMoney, result, "getInvestedMoney() does not return the correct invested amount!");
    }

    private Map<String, List<Investment>> getInvestmentsHistory() {
        Map<String, List<Investment>> investmentsHistory = new HashMap<>();
        investmentsHistory.computeIfAbsent("assetID", _ -> new ArrayList<>());

        List<Investment> list = investmentsHistory.get("assetID");
        list.add(Investment.of("assetID", 1, 50));
        list.add(Investment.of("assetID", 0.5, 100));

        return investmentsHistory;
    }

    private Map<String, Double> getHoldings() {
        Map<String, Double> holdings = new HashMap<>();
        holdings.put("assetID", 1.5);

        return holdings;
    }
}
