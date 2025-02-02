package server.coinapi.client;

import java.net.http.HttpClient;

public class CoinApiClient {

    private final HttpClient cryptoHttpClient;
    private final String apiKey;

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
}
