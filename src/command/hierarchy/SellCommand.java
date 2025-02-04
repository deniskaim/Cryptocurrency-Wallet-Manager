package command.hierarchy;

import exceptions.InsufficientFundsException;
import exceptions.InvalidAssetException;
import exceptions.MissingInWalletAssetException;
import exceptions.NotLoggedInException;
import exceptions.api.CryptoClientException;
import service.cryptowallet.CryptoWalletService;
import user.User;

import java.nio.channels.SelectionKey;

import static utils.TextUtils.getTheRestOfTheString;

public class SellCommand implements Command {

    private final String assetID;
    private final CryptoWalletService cryptoWalletService;
    private final SelectionKey selectionKey;

    private static final String OFFERING_CODE_INPUT_MESSAGE = "--offering=";
    private static final String SUCCESSFUL_MESSAGE = "You have successfully sold your %s for %f";

    public SellCommand(String[] args, CryptoWalletService cryptoWalletService, SelectionKey selectionKey) {
        if (args == null || args.length != 1) {
            throw new IllegalArgumentException(
                "args cannot be null reference and Sell command should contain exactly one specific argument!");
        }
        if (cryptoWalletService == null) {
            throw new IllegalArgumentException("cryptoWalletService cannot be null reference!");
        }
        if (selectionKey == null) {
            throw new IllegalArgumentException("selectionKey cannot be null reference!");
        }

        try {
            this.assetID = getTheRestOfTheString(args[0], OFFERING_CODE_INPUT_MESSAGE);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Offering code string is invalid!", e);
        }
        this.selectionKey = selectionKey;
        this.cryptoWalletService = cryptoWalletService;
    }

    @Override
    public String execute()
        throws NotLoggedInException, MissingInWalletAssetException {
        User user = (User) selectionKey.attachment();
        if (user == null) {
            throw new NotLoggedInException("Depositing money cannot happen before logging in!");
        }
        double newIncome = cryptoWalletService.sellCrypto(assetID, user.cryptoWallet());
        return String.format(SUCCESSFUL_MESSAGE, assetID, newIncome);
    }
}
