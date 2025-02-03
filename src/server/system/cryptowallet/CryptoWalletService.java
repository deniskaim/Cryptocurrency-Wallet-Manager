package server.system.cryptowallet;

import exceptions.api.CryptoClientException;
import server.coinapi.client.CoinApiClient;
import server.coinapi.dto.Asset;
import server.system.user.CryptoWallet;

import java.util.ArrayList;
import java.util.List;

public class CryptoWalletService {

    private final CoinApiClient coinApiClient;

    public CryptoWalletService(CoinApiClient coinApiClient) {
        if (coinApiClient == null) {
            throw new IllegalArgumentException("coinApiClient cannot be null reference!");
        }
        this.coinApiClient = coinApiClient;
    }

    public void depositMoneyInWallet(double amount, CryptoWallet cryptoWallet) {
        if (cryptoWallet == null) {
            throw new IllegalArgumentException("cryptoWallet cannot be null reference!");
        }
        cryptoWallet.depositMoney(amount);
    }

    public List<Offering> listOfferings() {
        try {
            List<Asset> assetList = coinApiClient.getAllAssets();
            return assetList.stream()
                .map(asset -> Offering.of(asset.assetID(), asset.price()))
                .toList();
        } catch (CryptoClientException e) {
            throw new RuntimeException("A problem occurred while trying to list the offerings", e);
        }
    }
}
