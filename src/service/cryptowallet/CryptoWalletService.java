package service.cryptowallet;

import exceptions.InsufficientFundsException;
import exceptions.InvalidAssetException;
import exceptions.api.CryptoClientException;
import coinapi.client.CoinApiClient;
import coinapi.dto.Asset;
import user.CryptoWallet;

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

    public double buyCrypto(double amountToSpend, String assetID, CryptoWallet cryptoWallet)
        throws InsufficientFundsException, InvalidAssetException, CryptoClientException {
        validateCryptoWallet(cryptoWallet);
        if (assetID == null) {
            throw new IllegalArgumentException("assetID cannot be null reference!");
        }

        Asset asset = getAssetByAssetID(assetID);

        double cryptoPrice = asset.price();
        double quantityToBuy = amountToSpend / cryptoPrice;

        cryptoWallet.withdrawMoney(amountToSpend);
        cryptoWallet.addQuantityToWallet(assetID, quantityToBuy);
        return quantityToBuy;
    }

    private void validateCryptoWallet(CryptoWallet cryptoWallet) {
        if (cryptoWallet == null) {
            throw new IllegalArgumentException("cryptoWallet cannot be null reference!");
        }
    }

    private Asset getAssetByAssetID(String assetID) throws CryptoClientException, InvalidAssetException {
        try {
            return coinApiClient.getAsset(assetID);
        } catch (InvalidAssetException e) {
            throw new RuntimeException("There is no asset with this offering_code!");
        } catch (CryptoClientException e) {
            throw new RuntimeException("A problem with the CoinAPI request occurred!");
        }
    }
}
