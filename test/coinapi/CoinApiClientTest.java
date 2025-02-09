package coinapi;

import coinapi.client.CoinApiClient;
import coinapi.dto.Asset;
import com.google.gson.Gson;
import exceptions.InvalidAssetException;
import exceptions.api.CryptoClientException;
import exceptions.api.apikey.InvalidApiKeyException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

public class CoinApiClientTest {

    private final HttpClient mockHttpClient = Mockito.mock(HttpClient.class);
    private final HttpResponse<String> mockResponse = Mockito.mock();
    private CoinApiClient coinApiClient;

    private static final Gson GSON = new Gson();
    private static final String API_KEY = "146865f1-12e8-4f3e-b75d-6d793420e4ae";

    @BeforeEach
    void setUp() throws IOException, InterruptedException {
        when(mockHttpClient.send(Mockito.any(HttpRequest.class),
            ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()))
            .thenReturn(mockResponse);

        when(mockResponse.body()).thenReturn("[]");
        when(mockResponse.statusCode()).thenReturn(HttpURLConnection.HTTP_OK);
        coinApiClient = new CoinApiClient(mockHttpClient, API_KEY);
    }

    @Test
    void testConstructorNullHttpClient() {
        assertThrows(IllegalArgumentException.class, () -> new CoinApiClient(null, ""),
            "An IllegalArgumentException is expected when the httpClient is null!");
    }

    @Test
    void testConstructorNullApiKey() {
        assertThrows(IllegalArgumentException.class, () -> new CoinApiClient(mockHttpClient, null),
            "An IllegalArgumentException is expected when the apiKey is null!");
    }

    @Test
    void testConstructorEmptyApiKey() {
        assertThrows(IllegalArgumentException.class, () -> new CoinApiClient(mockHttpClient, ""),
            "An IllegalArgumentException is expected when the apiKey is an empty string!");
    }

    @Test
    void testGetAssetsFromAPIWhenInvalidApiKey() {
        when(mockResponse.statusCode()).thenReturn(HttpURLConnection.HTTP_UNAUTHORIZED);

        assertThrows(InvalidApiKeyException.class, () -> coinApiClient.getAssetsFromAPI(),
            "An InvalidApiKeyException is expected when the apiKey is invalid!");
    }

    @Test
    void testGetAssetsFromAPIWhenIOException() throws Exception {
        when(mockHttpClient.send(Mockito.any(HttpRequest.class), ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()))
            .thenThrow(IOException.class);

        assertThrows(CryptoClientException.class, () -> coinApiClient.getAssetsFromAPI(),
            "An CryptoClientException is expected when an IOException occurs!");
    }

    @Test
    void testGetAssetsCorrectExecution() {
        Asset asset1 = createTestAsset("testAsset", 10);
        Asset asset2 = createTestAsset("testAsset2", 20);
        List<Asset> expectedResult = List.of(asset1, asset2);
        String assetsJson = GSON.toJson(expectedResult);

        when(mockResponse.body()).thenReturn(assetsJson);
        when(mockResponse.statusCode()).thenReturn(HttpURLConnection.HTTP_OK);

        List<Asset> result = coinApiClient.getAssetsFromAPI();

        assertEquals(expectedResult, result, "getAssets doesn't return the correct asset list!");
    }

    private Asset createTestAsset(String assetID, double price) {
        return new Asset(
            assetID, null, 1, null, null,
            null, null, null, null,
            null, null, null, null,
            null, null, price, null);
    }
}
