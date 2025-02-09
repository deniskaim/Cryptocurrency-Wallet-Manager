package cryptowallet;

import coinapi.client.CoinApiClient;
import coinapi.dto.Asset;
import exceptions.InvalidAssetException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.lang.Math.min;

public class AssetStorage implements AutoCloseable {

    private static final int MAX_ASSETS_COUNT = 50;
    private static final int UPDATE_CRYPTO_ASSETS_INTERVAL = 30; // в минути

    private final CoinApiClient coinApiClient;
    private final Map<String, Asset> allAssets = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor();

    public AssetStorage(CoinApiClient coinApiClient) {
        if (coinApiClient == null) {
            throw new IllegalArgumentException("coinApiClient cannot be null reference!");
        }
        this.coinApiClient = coinApiClient;
        startUpdatingAssets();
    }

    private void startUpdatingAssets() {
        scheduledExecutor.scheduleAtFixedRate(this::updateAssets, 0, UPDATE_CRYPTO_ASSETS_INTERVAL, TimeUnit.MINUTES);
    }

    public List<Asset> getAllAssets() {
        return new ArrayList<>(allAssets.values());
    }

    public Asset getAsset(String assetID) throws InvalidAssetException {
        if (assetID == null) {
            throw new IllegalArgumentException("assetID cannot be null reference!");
        }
        if (!allAssets.containsKey(assetID)) {
            throw new InvalidAssetException("There is no asset with this assetID!");
        }
        return allAssets.get(assetID);
    }

    private void updateAssets() {

        List<Asset> assetList = coinApiClient.getAssetsFromAPI();
        int countToReturn = min(assetList.size(), MAX_ASSETS_COUNT);

        Map<String, Asset> updatedAssets = new ConcurrentHashMap<>();
        assetList.stream()
            .filter(asset -> asset.typeIsCrypto() == 1 && Double.compare(asset.price(), 0d) == 1)
            .limit(countToReturn)
            .forEach(asset -> updatedAssets.put(asset.assetID(), asset));

        allAssets.keySet().removeIf(key -> !updatedAssets.containsKey(key));
        allAssets.putAll(updatedAssets);
    }

    @Override
    public void close() {
        scheduledExecutor.shutdown();
    }
}
