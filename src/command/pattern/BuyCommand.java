package command.pattern;

import exceptions.InvalidAssetException;
import exceptions.command.UnsuccessfulCommandException;
import exceptions.wallet.InsufficientFundsException;
import exceptions.user.NotLoggedInException;
import cryptowallet.CryptoWalletService;
import user.User;

import java.nio.channels.SelectionKey;

public class BuyCommand implements Command {

    private final String assetID;
    private final double amount;
    private final CryptoWalletService cryptoWalletService;
    private final SelectionKey selectionKey;

    private static final String SUCCESSFUL_MESSAGE = "You have successfully bought %f of %s";

    public BuyCommand(String assetID, double amount, CryptoWalletService cryptoWalletService,
                      SelectionKey selectionKey) {
        if (assetID == null) {
            throw new IllegalArgumentException("assetID cannot be null reference!");
        }
        if (cryptoWalletService == null) {
            throw new IllegalArgumentException("cryptoWalletService cannot be null reference!");
        }
        if (selectionKey == null) {
            throw new IllegalArgumentException("selectionKey cannot be null reference!");
        }
        if (Double.compare(amount, 0d) <= 0) {
            throw new IllegalArgumentException("The amount in the buy-money command cannot be below 0.00 USD!");
        }
        this.assetID = assetID;
        this.selectionKey = selectionKey;
        this.cryptoWalletService = cryptoWalletService;
        this.amount = amount;
    }

    @Override
    public String execute() throws UnsuccessfulCommandException {
        try {
            User user = (User) selectionKey.attachment();
            if (user == null) {
                throw new NotLoggedInException("Buying cannot happen before logging in!");
            }
            double boughtQuantity = cryptoWalletService.buyCrypto(amount, assetID, user.cryptoWallet());
            return String.format(SUCCESSFUL_MESSAGE, boughtQuantity, assetID);
        } catch (NotLoggedInException | InsufficientFundsException | InvalidAssetException e) {
            throw new UnsuccessfulCommandException("Buy command is unsuccessful! " + e.getMessage(), e);
        }
    }
}
