package coinapi.client;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import exceptions.api.CryptoClientException;
import exceptions.api.apikey.InvalidApiKeyException;
import coinapi.dto.Asset;

import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class CoinApiClient {

    private static final String SCHEME = "https";
    private static final String HOST = "rest.coinapi.io";
    private static final String PATH_ALL_ASSETS = "/v1/assets";
    private static final String QUERY = "apikey=%s";

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

    public List<Asset> getAssetsFromAPI() {
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

    private List<Asset> getAssetsFromResponse(HttpResponse<String> response) {
        Type listType = new TypeToken<List<Asset>>() {
        }.getType();
        List<Asset> assets = GSON.fromJson(response.body(), listType);

        if (response.statusCode() == HttpURLConnection.HTTP_UNAUTHORIZED) {
            throw new InvalidApiKeyException("ApiKey is invalid! Try with another one!");
        }
        return assets;
    }
}
