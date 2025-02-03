package server.system.cryptowallet;

import exceptions.InsufficientFundsException;
import exceptions.InvalidAssetException;
import exceptions.api.CryptoClientException;
import coinapi.client.CoinApiClient;
import coinapi.dto.Asset;
import server.system.user.CryptoWallet;

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
        validateCryptoWallet(cryptoWallet);
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

    public void buyCrypto(double amountToSpend, String assetID, CryptoWallet cryptoWallet)
        throws InsufficientFundsException, InvalidAssetException {
        validateCryptoWallet(cryptoWallet);
        if (assetID == null) {
            throw new IllegalArgumentException("assetID cannot be null reference!");
        }

        if (!cryptoWallet.isAbleToSpend(amountToSpend)) {
            throw new InsufficientFundsException(
                "The balance in the CryptoWallet is lower than the desired amount to spend");
        }

        Asset asset = getAssetByAssetID(assetID);
        if (!asset.assetID().equals(assetID)) {
            throw new InvalidAssetException("There is no asset with this offering_code!");
        }
        double cryptoPrice = asset.price();
        double quantityToBuy = amountToSpend / cryptoPrice;

        cryptoWallet.addQuantityToWallet(assetID, quantityToBuy);
    }

    private void validateCryptoWallet(CryptoWallet cryptoWallet) {
        if (cryptoWallet == null) {
            throw new IllegalArgumentException("cryptoWallet cannot be null reference!");
        }
    }

    private Asset getAssetByAssetID(String assetID) {
        try {
            return coinApiClient.getAsset(assetID);
        } catch (CryptoClientException e) {
            throw new RuntimeException("Unsuccessful try to get the asset information from the API!", e);
        }
    }
}
