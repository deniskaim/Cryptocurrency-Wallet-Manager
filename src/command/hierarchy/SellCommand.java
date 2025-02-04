package command.hierarchy;

import exceptions.InvalidAssetException;
import exceptions.command.IncorrectArgumentsCountException;
import exceptions.command.UnsuccessfulCommandException;
import exceptions.wallet.MissingInWalletAssetException;
import exceptions.user.NotLoggedInException;
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

    public SellCommand(String[] args, CryptoWalletService cryptoWalletService, SelectionKey selectionKey)
        throws IncorrectArgumentsCountException {
        if (args == null) {
            throw new IllegalArgumentException("args cannot be null reference");
        }
        if (args.length != 1) {
            throw new IncorrectArgumentsCountException("Sell command should contain exactly one specific argument!");
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
