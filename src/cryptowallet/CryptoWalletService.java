package cryptowallet;

import cryptowallet.offers.CryptoCatalog;
import cryptowallet.offers.Offering;
import cryptowallet.summary.CryptoWalletOverallSummary;
import exceptions.wallet.InsufficientFundsException;
import exceptions.InvalidAssetException;
import exceptions.wallet.MissingInWalletAssetException;
import exceptions.wallet.NoActiveInvestmentsException;
import coinapi.dto.Asset;
import storage.AssetStorage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CryptoWalletService {

    private final AssetStorage assetStorage;

    public CryptoWalletService(AssetStorage assetStorage) {
        if (assetStorage == null) {
            throw new IllegalArgumentException("assetStorage cannot be null reference!");
        }
        this.assetStorage = assetStorage;
    }

    public CryptoCatalog getCryptoCatalogWithOfferings() {
        List<Asset> assets = assetStorage.getAllAssets();
        List<Offering> offerings = assets.stream()
            .map(asset -> Offering.of(asset.assetID(), asset.name(), asset.price()))
            .toList();

        return new CryptoCatalog(offerings);
    }

    public double buyCrypto(double amountToSpend, String assetID, CryptoWallet cryptoWallet)
        throws InsufficientFundsException, InvalidAssetException {
        validateCryptoWallet(cryptoWallet);
        validateAssetID(assetID);

        Asset asset = getAssetByAssetID(assetID);

        double assetPrice = asset.price();
        double quantityToBuy = amountToSpend / assetPrice;

        cryptoWallet.withdrawMoney(amountToSpend);
        cryptoWallet.addInvestment(assetID, quantityToBuy, assetPrice);

        return quantityToBuy;
    }

    public double sellCrypto(String assetID, CryptoWallet cryptoWallet)
        throws MissingInWalletAssetException, InvalidAssetException {
        validateCryptoWallet(cryptoWallet);
        validateAssetID(assetID);

        Asset asset = getAssetByAssetID(assetID);

        double assetPrice = asset.price();
        double quantityInWallet = cryptoWallet.removeInvestment(assetID);

        double income = assetPrice * quantityInWallet;
        cryptoWallet.depositMoney(income);

        return income;
    }

    public CryptoWalletOverallSummary getWalletOverallSummary(CryptoWallet cryptoWallet)
        throws NoActiveInvestmentsException, InvalidAssetException {
        validateCryptoWallet(cryptoWallet);

        Map<String, Double> accountHoldings = cryptoWallet.getHoldings();
        if (accountHoldings.isEmpty()) {
            throw new NoActiveInvestmentsException("There are no active investments in the wallet!");
        }

        Map<String, Double> assetsProfit = new HashMap<>();

        double overallProfit = 0;
        for (String assetID : accountHoldings.keySet()) {
            double currentAssetQuantity = accountHoldings.get(assetID);
            double assetPrice = getAssetByAssetID(assetID).price();

            double investedMoneyInCryptoAsset = cryptoWallet.getInvestedMoneyInCryptoByAssetID(assetID);
            double sellValueOfCryptoAsset = assetPrice * currentAssetQuantity;
            double profit = sellValueOfCryptoAsset - investedMoneyInCryptoAsset;

            assetsProfit.put(assetID, profit);
            overallProfit += profit;
        }

        return new CryptoWalletOverallSummary(overallProfit, assetsProfit);
    }

    private void validateCryptoWallet(CryptoWallet cryptoWallet) {
        if (cryptoWallet == null) {
            throw new IllegalArgumentException("cryptoWallet cannot be null reference!");
        }
    }

    private void validateAssetID(String assetID) {
        if (assetID == null) {
            throw new IllegalArgumentException("assetID cannot be null reference!");
        }
    }

    private Asset getAssetByAssetID(String assetID) throws InvalidAssetException {
        return assetStorage.getAsset(assetID);
    }
}
