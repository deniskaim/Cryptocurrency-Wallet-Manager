package coinapi.client;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import exceptions.InvalidAssetException;
import exceptions.api.CryptoClientException;
import exceptions.api.apikey.InvalidApiKeyException;
import coinapi.dto.Asset;

import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.lang.Math.min;

public class CoinApiClient implements AutoCloseable {

    private static final String SCHEME = "https";
    private static final String HOST = "rest.coinapi.io";
    private static final String PATH_ALL_ASSETS = "/v1/assets";
    private static final String QUERY = "apikey=%s";

    private static final int MAX_ASSETS_COUNT = 50;
    private static final int UPDATE_CRYPTO_ASSETS_INTERVAL = 30;

    private final HttpClient cryptoHttpClient;
    private final String apiKey;

    private final Map<String, Asset> allAssets = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor();

    private static final Gson GSON = new Gson();

    public CoinApiClient(HttpClient cryptoHttpClient, String apiKey) {
        if (cryptoHttpClient == null) {
            throw new IllegalArgumentException("cryptoHttpClient cannot be null reference!");
        }
        if (apiKey == null || apiKey.isEmpty()) {
            throw new IllegalArgumentException("apiKey cannot be null reference or empty!");
        }
        this.cryptoHttpClient = cryptoHttpClient;
        this.apiKey = apiKey;

        startUpdatingAssets();
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

    @Override
    public void close() {
        scheduledExecutor.shutdown();
    }

    private void startUpdatingAssets() {
        scheduledExecutor.scheduleAtFixedRate(() -> {
            try {
                updateAssets();
            } catch (InvalidApiKeyException e) {
                throw new RuntimeException("ApiKey is invalid! Try with another one!", e);
            } catch (CryptoClientException e) {
                throw new RuntimeException("A problem with the CoinAPI request occurred!", e);
            }
        }, 0, UPDATE_CRYPTO_ASSETS_INTERVAL, TimeUnit.MINUTES);
    }

    private void updateAssets() throws CryptoClientException {
        List<Asset> assetList = getAssetsFromAPI();
        int countToReturn = min(assetList.size(), MAX_ASSETS_COUNT);

        Map<String, Asset> updatedAssets = new ConcurrentHashMap<>();
        assetList.stream()
            .filter(asset -> asset.typeIsCrypto() == 1 && Double.compare(asset.price(), 0d) == 1)
            .limit(countToReturn)
            .forEach(asset -> updatedAssets.put(asset.assetID(), asset));

        allAssets.putAll(updatedAssets);
    }

    private List<Asset> getAssetsFromAPI() throws CryptoClientException {
        HttpResponse<String> responseFromAPI;
        try {
            URI uri = new URI(SCHEME, HOST, PATH_ALL_ASSETS, String.format(QUERY, apiKey), null);
            HttpRequest httpRequest = HttpRequest.newBuilder().uri(uri).build();
            responseFromAPI = cryptoHttpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            throw new CryptoClientException("Could not retrieve the assets!", e);
        }

        return getAssetsFromResponse(responseFromAPI);
    }

    private List<Asset> getAssetsFromResponse(HttpResponse<String> response) throws InvalidApiKeyException {
        Type listType = new TypeToken<List<Asset>>() {
        }.getType();
        List<Asset> assets = GSON.fromJson(response.body(), listType);

        if (response.statusCode() == HttpURLConnection.HTTP_UNAUTHORIZED) {
            throw new InvalidApiKeyException("ApiKey is invalid! Try with another one!");
        }
        return assets;
    }
}
