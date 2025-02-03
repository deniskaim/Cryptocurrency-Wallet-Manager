package server.command.hierarchy;

import exceptions.NotLoggedInException;
import server.system.cryptowallet.CryptoWalletService;
import server.system.user.User;

import java.nio.channels.SelectionKey;

public class DepositMoneyCommand implements Command {

    private final double amount;
    private final SelectionKey selectionKey;
    private final CryptoWalletService cryptoWalletService;

    private static final String SUCCESSFUL_MESSAGE = "You have successfully made a deposit of %f";

    public DepositMoneyCommand(String[] args, CryptoWalletService cryptoWalletService, SelectionKey selectionKey) {
        if (args == null || args.length != 1) {
            throw new IllegalArgumentException("DepositMoney should be include only one argument - the amount");
        }
        if (cryptoWalletService == null) {
            throw new IllegalArgumentException("cryptoWalletService cannot be null reference!");
        }
        if (selectionKey == null) {
            throw new IllegalArgumentException("selectionKey cannot be null reference!");
        }

        try {
            this.amount = Double.parseDouble(args[0]);
            if (this.amount < 0) {
                throw new IllegalArgumentException("The amount in the deposit-money command cannot be below 0.00 USD");
            }
            this.selectionKey = selectionKey;
            this.cryptoWalletService = cryptoWalletService;
        } catch (NumberFormatException e) {
            throw new RuntimeException("The amount in the deposit-money command is not in an appropriate format", e);
        }
    }

    @Override
    public String execute() throws NotLoggedInException {
        User user = (User) selectionKey.attachment();
        if (user == null) {
            throw new NotLoggedInException("Depositing money cannot happen before logging in!");
        }
        cryptoWalletService.depositMoneyInWallet(amount, user.cryptoWallet());
        return String.format(SUCCESSFUL_MESSAGE, amount);
    }
}
