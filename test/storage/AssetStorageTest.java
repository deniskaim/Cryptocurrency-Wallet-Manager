package storage;

import coinapi.client.CoinApiClient;
import coinapi.dto.Asset;
import exceptions.InvalidAssetException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

public class AssetStorageTest {

    private final CoinApiClient mockCoinApiClient = Mockito.mock(CoinApiClient.class);
    private List<Asset> testAssetList;
    private AssetStorage assetStorage;

    @BeforeEach
    void setUp() {
        Asset testAsset1 = createTestAsset("BTC", 95000);
        Asset testAsset2 = createTestAsset("ETH", 2700);
        testAssetList = List.of(testAsset1, testAsset2);
        when(mockCoinApiClient.getAssetsFromAPI()).thenReturn(testAssetList);

        assetStorage = new AssetStorage(mockCoinApiClient);
    }

    @AfterEach
    void tearDown() {
        assetStorage.close();
    }

    @Test
    void testConstructorNullCoinApiClient() {
        assertThrows(IllegalArgumentException.class, () -> new AssetStorage(null),
            "An IllegalArgumentException is expected when the assetStorage is null reference!");
    }

    @Test
    void testConstructorShouldCreateValidAssets() {
        List<Asset> result = assetStorage.getAllAssets();
        assertEquals(2, result.size(), "the assetStorage does not save the assets correctly!");
        assertEquals(testAssetList, result, "the assetStorage does not return the correct asset!");
    }

    @Test
    void testConstructorShouldRemoveAssetsWithPriceZero() {
        List<Asset> assetList = List.of(createTestAsset("testAsset", 0));
        when(mockCoinApiClient.getAssetsFromAPI()).thenReturn(assetList);
        AssetStorage instance = new AssetStorage(mockCoinApiClient);

        List<Asset> result = instance.getAllAssets();
        assertTrue(result.isEmpty(), "There should not be any assets in the storage!");
    }

    @Test
    void testGetAssetNullAssetID() {
        assertThrows(IllegalArgumentException.class, () -> assetStorage.getAsset(null),
            "An IllegalArgumentException is expected when the assetID is null reference!");
    }

    @Test
    void testGetAssetInvalid() {
        assertThrows(InvalidAssetException.class, () -> assetStorage.getAsset("invalid assetID"),
            "An InvalidAssetException is expected when the assetID is invalid");
    }

    @Test
    void testGetAsset() throws InvalidAssetException {
        Asset result = assetStorage.getAsset("BTC");

        assertTrue(testAssetList.contains(result), "getAsset() does not return the correct asset!");
    }

    @Test
    void testGetAllAssets() {
        List<Asset> result = assetStorage.getAllAssets();

        assertEquals(testAssetList, result, "getAllAssets() does not return the correct assetList!");
    }

    private Asset createTestAsset(String assetID, double price) {
        return new Asset(
            assetID, null, 1, null, null,
            null, null, null, null,
            null, null, null, null,
            null, null, price, null);
    }
}
