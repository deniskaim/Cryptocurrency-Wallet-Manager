package cryptowallet;

import exceptions.wallet.InsufficientFundsException;
import exceptions.InvalidAssetException;
import exceptions.wallet.MissingInWalletAssetException;
import exceptions.wallet.NoActiveInvestmentsException;
import coinapi.client.CoinApiClient;
import coinapi.dto.Asset;

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
        double quantityInWallet = cryptoWallet.getQuantityOfAsset(assetID);
        double income = assetPrice * quantityInWallet;

        cryptoWallet.removeInvestment(assetID);
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
            double currentAssetPrice = getAssetByAssetID(assetID).price();

            estimatedSellValue += currentAssetPrice * currentAssetQuantity;
        }

        return estimatedSellValue - investedValue;
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
        return coinApiClient.getAsset(assetID);
    }
}
