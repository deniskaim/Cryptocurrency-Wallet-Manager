package command.pattern;

import exceptions.InvalidAssetException;
import exceptions.command.UnsuccessfulCommandException;
import exceptions.wallet.MissingInWalletAssetException;
import exceptions.user.NotLoggedInException;
import cryptowallet.CryptoWalletService;
import user.User;

import java.nio.channels.SelectionKey;

public class SellCommand implements Command {

    private final String assetID;
    private final CryptoWalletService cryptoWalletService;
    private final SelectionKey selectionKey;

    private static final String SUCCESSFUL_MESSAGE = "You have successfully sold your %s for %f USD";

    public SellCommand(String assetID, CryptoWalletService cryptoWalletService, SelectionKey selectionKey) {
        if (assetID == null) {
            throw new IllegalArgumentException("assetID cannot be null reference");
        }
        if (cryptoWalletService == null) {
            throw new IllegalArgumentException("cryptoWalletService cannot be null reference!");
        }
        if (selectionKey == null) {
            throw new IllegalArgumentException("selectionKey cannot be null reference!");
        }

        this.assetID = assetID;
        this.selectionKey = selectionKey;
        this.cryptoWalletService = cryptoWalletService;
    }

    @Override
    public String execute()
        throws UnsuccessfulCommandException {
        try {
            User user = (User) selectionKey.attachment();
            if (user == null) {
                throw new NotLoggedInException("Selling crypto cannot happen before logging in!");
            }
            double newIncome = cryptoWalletService.sellCrypto(assetID, user.cryptoWallet());
            return String.format(SUCCESSFUL_MESSAGE, assetID, newIncome);
        } catch (NotLoggedInException | MissingInWalletAssetException | InvalidAssetException e) {
            throw new UnsuccessfulCommandException("Sell command is unsuccessful! " + e.getMessage(), e);
        }
    }
}
