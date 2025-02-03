package command.hierarchy;

import exceptions.InsufficientFundsException;
import exceptions.InvalidAssetException;
import exceptions.NotLoggedInException;
import server.system.cryptowallet.CryptoWalletService;
import server.system.user.User;

import java.nio.channels.SelectionKey;

import static utils.TextUtils.getTheRestOfTheString;

public class BuyCommand implements Command {

    private final String assetID;
    private final double amount;
    private final CryptoWalletService cryptoWalletService;
    private final SelectionKey selectionKey;

    private static final String OFFERING_CODE_INPUT_MESSAGE = "--offering=";
    private static final String MONEY_INPUT_MESSAGE = "--money=";
    private static final String SUCCESSFUL_MESSAGE = "You have successfully invested %f USD in %s";

    public BuyCommand(String[] args, CryptoWalletService cryptoWalletService, SelectionKey selectionKey) {
        if (args == null || args.length != 2) {
            throw new IllegalArgumentException(
                "args cannot be null reference and Buy command should contain exactly two specific arguments");
        }
        if (cryptoWalletService == null) {
            throw new IllegalArgumentException("cryptoWalletService cannot be null reference!");
        }
        if (selectionKey == null) {
            throw new IllegalArgumentException("selectionKey cannot be null reference!");
        }

        this.assetID = getTheRestOfTheString(args[0], OFFERING_CODE_INPUT_MESSAGE);
        this.selectionKey = selectionKey;
        this.cryptoWalletService = cryptoWalletService;
        try {
            this.amount = Double.parseDouble(getTheRestOfTheString(args[1], MONEY_INPUT_MESSAGE));
            if (this.amount < 0) {
                throw new IllegalArgumentException("The amount in the buy-money command cannot be below 0.00 USD");
            }
        } catch (NumberFormatException e) {
            throw new RuntimeException("The amount in the buy-money command is not in an appropriate format", e);
        }
    }

    @Override
    public String execute() throws NotLoggedInException, InsufficientFundsException, InvalidAssetException {
        User user = (User) selectionKey.attachment();
        if (user == null) {
            throw new NotLoggedInException("Depositing money cannot happen before logging in!");
        }
        cryptoWalletService.buyCrypto(amount, assetID, user.cryptoWallet());
        return String.format(SUCCESSFUL_MESSAGE, amount, assetID);
    }
}
