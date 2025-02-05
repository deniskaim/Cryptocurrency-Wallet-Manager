package command.hierarchy;

import exceptions.InvalidAssetException;
import exceptions.command.IncorrectArgumentsCountException;
import exceptions.command.InvalidCommandException;
import exceptions.command.UnsuccessfulCommandException;
import exceptions.wallet.InsufficientFundsException;
import exceptions.user.NotLoggedInException;
import service.cryptowallet.CryptoWalletService;
import user.User;

import java.nio.channels.SelectionKey;

import static utils.TextUtils.getTheRestOfTheString;

public class BuyCommand implements Command {

    private final String assetID;
    private final double amount;
    private final CryptoWalletService cryptoWalletService;
    private final SelectionKey selectionKey;

    private static final String OFFERING_CODE_INPUT_MESSAGE = "--offering=";
    private static final String MONEY_INPUT_MESSAGE = "--money=";
    private static final String SUCCESSFUL_MESSAGE = "You have successfully bought %f of %s";

    public BuyCommand(String[] args, CryptoWalletService cryptoWalletService, SelectionKey selectionKey)
        throws IncorrectArgumentsCountException, InvalidCommandException {
        if (args == null) {
            throw new IllegalArgumentException(
                "args in BuyCommand cannot be null reference!");
        }
        if (args.length != 2) {
            throw new IncorrectArgumentsCountException("Buy command should contain exactly two specific arguments!");
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
            throw new InvalidCommandException("Offering code string is invalid!", e);
        }

        this.selectionKey = selectionKey;
        this.cryptoWalletService = cryptoWalletService;
        try {
            this.amount = Double.parseDouble(getTheRestOfTheString(args[1], MONEY_INPUT_MESSAGE));
            if (this.amount < 0) {
                throw new IllegalArgumentException("The amount in the buy-money command cannot be below 0.00 USD!");
            }
        } catch (NumberFormatException e) {
            throw new InvalidCommandException("The amount in the buy-money command is not in an appropriate format",
                e);
        } catch (IllegalArgumentException e) {
            throw new InvalidCommandException("Money string is invalid", e);
        }
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
