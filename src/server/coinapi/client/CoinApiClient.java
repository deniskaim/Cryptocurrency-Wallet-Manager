package server.coinapi.client;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import exceptions.api.CryptoClientException;
import exceptions.api.apikey.InvalidApiKeyException;
import server.coinapi.dto.Asset;

import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static java.lang.Math.min;

public class CoinApiClient {

    private static final String SCHEME = "https";
    private static final String HOST = "rest.coinapi.io";
    private static final String PATH_ALL_ASSETS = "/v1/assets";
    private static final String PATH_SINGLE_ASSET = "/v1/assets/%s";
    private static final String QUERY = "apikey=%s";

    private static final int MAX_ASSETS_COUNT = 50;

    private final HttpClient cryptoHttpClient;
    private final String apiKey;

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
    }

    //todo: Server has to cache the information from the API for 30 minutes.

    public List<Asset> getAllAssets() throws CryptoClientException {
        List<Asset> assetList = getAssetByPath(PATH_ALL_ASSETS);
        int countToReturn = min(assetList.size(), MAX_ASSETS_COUNT);
        return assetList.subList(0, countToReturn);
    }

    public Asset getAsset(String assetID) throws CryptoClientException {
        if (assetID == null) {
            throw new IllegalArgumentException("assetID cannot be null reference!");
        }
        List<Asset> assetsList = getAssetByPath(String.format(PATH_SINGLE_ASSET, assetID));
        return assetsList.getFirst();
    }

    private List<Asset> getAssetByPath(String path) throws CryptoClientException {
        HttpResponse<String> responseFromAPI;
        try {
            URI uri = new URI(SCHEME, HOST, path, String.format(QUERY, apiKey), null);
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
