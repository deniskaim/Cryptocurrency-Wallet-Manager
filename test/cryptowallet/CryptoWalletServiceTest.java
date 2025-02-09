package cryptowallet;

import coinapi.dto.Asset;
import cryptowallet.offers.CryptoCatalog;
import cryptowallet.offers.Offering;
import cryptowallet.summary.CryptoWalletOverallSummary;
import exceptions.InvalidAssetException;
import exceptions.wallet.InsufficientFundsException;
import exceptions.wallet.MissingInWalletAssetException;
import exceptions.wallet.NoActiveInvestmentsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import storage.AssetStorage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CryptoWalletServiceTest {

    CryptoWallet cryptoWallet = Mockito.mock(CryptoWallet.class);
    AssetStorage assetStorage = Mockito.mock(AssetStorage.class);
    CryptoWalletService cryptoWalletService;

    @BeforeEach
    void setUp() {
        cryptoWalletService = new CryptoWalletService(assetStorage);
    }

    private Asset createAsset(String assetID, String name, double price) {
        return new Asset(
            assetID, name, 1, null, null,
            null, null, null, null,
            null, null, null, null,
            null, null, price, null);
    }

    @Test
    void testListOfferings() {
        Asset customAsset1 = createAsset("customAsset1", "customAsset1", 10.0);
        Asset customAsset2 = createAsset("customAsset2", "customAsset2", 20.0);

        when(assetStorage.getAllAssets()).thenReturn(List.of(customAsset1, customAsset2));
        List<Offering> expectedOfferings =
            List.of(Offering.of("customAsset1", "customAsset1", 10.0),
                Offering.of("customAsset2", "customAsset2", 20.0));

        CryptoCatalog expectedCatalog = CryptoCatalog.of(expectedOfferings);
        CryptoCatalog resultCatalog = cryptoWalletService.getCryptoCatalogWithOfferings();
        assertEquals(expectedCatalog, resultCatalog, "listOfferings() does not return the correct catalog!");
    }

    @Test
    void testBuyCryptoNullCryptoWallet() {
        assertThrows(IllegalArgumentException.class,
            () -> cryptoWalletService.buyCrypto(10, "customAsset1", null),
            "An IllegalArgumentException is expected when cryptoWallet is null reference!");
    }

    @Test
    void testBuyCryptoNullAssetID() {
        assertThrows(IllegalArgumentException.class,
            () -> cryptoWalletService.buyCrypto(10, null, new CryptoWallet()),
            "An IllegalArgumentException is expected when assetID is null reference!");
    }

    @Test
    void testBuyCryptoWhenInvalidAssetID() throws InvalidAssetException {
        when(assetStorage.getAsset("customAsset1")).thenThrow(InvalidAssetException.class);

        assertThrows(InvalidAssetException.class,
            () -> cryptoWalletService.buyCrypto(10, "customAsset1", new CryptoWallet()),
            "An InvalidAssetException is expected when assetID is invalid!");
    }

    @Test
    void testBuyCryptoWhenInsufficientFundsException() throws InsufficientFundsException, InvalidAssetException {
        when(assetStorage.getAsset("customAsset1")).thenReturn(createAsset("customAsset1", "customAsset1", 5));
        doThrow(InsufficientFundsException.class).when(cryptoWallet).withdrawMoney(10);

        assertThrows(InsufficientFundsException.class,
            () -> cryptoWalletService.buyCrypto(10, "customAsset1", cryptoWallet),
            "An InsufficientFundsException is expected when the funds are insufficient!");
    }

    @Test
    void testBuyCrypto() throws InvalidAssetException, InsufficientFundsException {
        final double assetPrice = 100;
        when(assetStorage.getAsset("customAsset1")).thenReturn(createAsset("customAsset1", "customAsset1", assetPrice));

        final double expectedBoughtQuantity = 2.5;
        double boughtQuantity = cryptoWalletService.buyCrypto(250, "customAsset1", cryptoWallet);

        verify(cryptoWallet, times(1)).withdrawMoney(250);
        verify(cryptoWallet, times(1)).addInvestment("customAsset1", 2.5, 100);
        assertEquals(expectedBoughtQuantity, boughtQuantity, "buyCrypto() does not buy the correct quantity!");
    }

    @Test
    void testSellCryptoNullCryptoWallet() {
        assertThrows(IllegalArgumentException.class,
            () -> cryptoWalletService.sellCrypto("customAsset1", null),
            "An IllegalArgumentException is expected when cryptoWallet is null reference!");
    }

    @Test
    void testSellCryptoNullAssetID() {
        assertThrows(IllegalArgumentException.class,
            () -> cryptoWalletService.sellCrypto(null, new CryptoWallet()),
            "An IllegalArgumentException is expected when assetID is null reference!");
    }

    @Test
    void testSellCryptoWhenInvalidAssetID() throws InvalidAssetException {
        when(assetStorage.getAsset("customAsset1")).thenThrow(InvalidAssetException.class);

        assertThrows(InvalidAssetException.class,
            () -> cryptoWalletService.sellCrypto("customAsset1", new CryptoWallet()),
            "An InvalidAssetException is expected when assetID is invalid!");
    }

    @Test
    void testSellCryptoWhenMissingInWalletAssetException() throws InvalidAssetException,
        MissingInWalletAssetException {
        when(assetStorage.getAsset("customAsset1")).thenReturn(createAsset("customAsset1", "customAsset1", 5));
        doThrow(MissingInWalletAssetException.class).when(cryptoWallet).removeInvestment("customAsset1");

        assertThrows(MissingInWalletAssetException.class,
            () -> cryptoWalletService.sellCrypto("customAsset1", cryptoWallet),
            "A MissingInWalletAssetException is expected when the asset cannot be found in the wallet's investments!");
    }

    @Test
    void testSellCrypto() throws InvalidAssetException, MissingInWalletAssetException {
        final double currentAssetPrice = 100;
        when(assetStorage.getAsset("customAsset1")).thenReturn(
            createAsset("customAsset1", "customAsset1", currentAssetPrice));

        final double assetQuantityInWallet = 2.5;
        when(cryptoWallet.removeInvestment("customAsset1")).thenReturn(assetQuantityInWallet);

        final double expectedIncome = 250;
        double resultIncome = cryptoWalletService.sellCrypto("customAsset1", cryptoWallet);

        verify(cryptoWallet, times(1)).depositMoney(250);
        verify(cryptoWallet, times(1)).removeInvestment("customAsset1");
        assertEquals(expectedIncome, resultIncome, "sellCrypto() does not calculate the income correctly!");
    }

    @Test
    void testGetWalletOverallSummaryNullCryptoWallet() {
        assertThrows(IllegalArgumentException.class,
            () -> cryptoWalletService.getWalletOverallSummary(null),
            "An IllegalArgumentException is expected when cryptoWallet is null reference!");
    }

    @Test
    void testGetWalletOverallSummaryWhenNoActiveInvestments() {
        Map<String, Double> emptyHoldings = new HashMap<>();
        when(cryptoWallet.getHoldings()).thenReturn(emptyHoldings);

        assertThrows(NoActiveInvestmentsException.class,
            () -> cryptoWalletService.getWalletOverallSummary(cryptoWallet),
            "A NoActiveInvestmentsException is expected when there are no investments!");

    }

    @Test
    void testGetWalletOverallSummary() throws InvalidAssetException, NoActiveInvestmentsException {
        Map<String, Double> holdings = new HashMap<>();
        holdings.put("asset1", 1.5);
        holdings.put("asset2", 1d);
        when(cryptoWallet.getHoldings()).thenReturn(holdings);

        when(cryptoWallet.getInvestedMoneyInCryptoByAssetID("asset1")).thenReturn(4d);
        when(cryptoWallet.getInvestedMoneyInCryptoByAssetID("asset2")).thenReturn(6d);

        final double price1 = 6;
        final double price2 = 7;
        when(assetStorage.getAsset("asset1")).thenReturn(createAsset("asset1", "asset1", price1));
        when(assetStorage.getAsset("asset2")).thenReturn(createAsset("asset2", "asset2", price2));

        double expectedProfit = 6;

        CryptoWalletOverallSummary result = cryptoWalletService.getWalletOverallSummary(cryptoWallet);
        assertEquals(expectedProfit, result.getOverallProfitLoss(),
            "getWalletOverallSummary doesn't calculate the profit correctly!");

        assertEquals(expectedProfit, result.getAssetsProfit().get("asset1") + result.getAssetsProfit().get("asset2"),
            "getWalletOverallSummary doesn't calculate the profit of the investments correctly!");
    }
}
