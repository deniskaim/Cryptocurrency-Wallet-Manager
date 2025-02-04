package service.cryptowallet;

import exceptions.wallet.InsufficientFundsException;
import exceptions.InvalidAssetException;
import exceptions.wallet.MissingInWalletAssetException;
import exceptions.wallet.NoActiveInvestmentsException;
import coinapi.client.CoinApiClient;
import coinapi.dto.Asset;
import user.CryptoWallet;

import java.util.List;
import java.util.Map;

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
        List<Asset> assetList = coinApiClient.getAllAssets();
        return assetList.stream()
            .map(asset -> Offering.of(asset.assetID(), asset.price()))
            .toList();
    }

    public double buyCrypto(double amountToSpend, String assetID, CryptoWallet cryptoWallet)
        throws InsufficientFundsException, InvalidAssetException {
        validateCryptoWallet(cryptoWallet);
        if (assetID == null) {
            throw new IllegalArgumentException("assetID cannot be null reference!");
        }

        Asset asset = getAssetByAssetID(assetID);

        double cryptoPrice = asset.price();
        double quantityToBuy = amountToSpend / cryptoPrice;

        cryptoWallet.withdrawMoney(amountToSpend);
        cryptoWallet.addQuantityToWallet(assetID, quantityToBuy);
        cryptoWallet.setPurchasePriceOfAsset(assetID, cryptoPrice);

        return quantityToBuy;
    }

    public double sellCrypto(String assetID, CryptoWallet cryptoWallet)
        throws MissingInWalletAssetException, InvalidAssetException {
        validateCryptoWallet(cryptoWallet);
        if (assetID == null) {
            throw new IllegalArgumentException("assetID cannot be null reference!");
        }

        Asset asset = getAssetByAssetID(assetID);
        double cryptoPrice = asset.price();

        double quantityInWallet = cryptoWallet.getQuantityOfAsset(assetID);
        cryptoWallet.removeAsset(assetID);

        double income = cryptoPrice * quantityInWallet;
        cryptoWallet.depositMoney(income);
        return income;
    }

    public double accumulateProfitLoss(CryptoWallet cryptoWallet)
        throws NoActiveInvestmentsException, InvalidAssetException {
        validateCryptoWallet(cryptoWallet);

        Map<String, Double> accountHoldings = cryptoWallet.getHoldings();
        if (accountHoldings.isEmpty()) {
            throw new NoActiveInvestmentsException("There are no active investments in the wallet!");
        }

        double investedValue = cryptoWallet.getInvestedMoney();
        double estimatedSellValue = 0;
        for (String assetID : accountHoldings.keySet()) {
            double currentAssetQuantity = accountHoldings.get(assetID);
            estimatedSellValue += getAssetByAssetID(assetID).price() * currentAssetQuantity;
        }

        return estimatedSellValue - investedValue;
    }

    private void validateCryptoWallet(CryptoWallet cryptoWallet) {
        if (cryptoWallet == null) {
            throw new IllegalArgumentException("cryptoWallet cannot be null reference!");
        }
    }

    private Asset getAssetByAssetID(String assetID) throws InvalidAssetException {
        return coinApiClient.getAsset(assetID);
    }
}
