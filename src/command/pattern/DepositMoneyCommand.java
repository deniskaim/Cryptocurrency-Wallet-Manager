package command.pattern;

import exceptions.command.IncorrectArgumentsCountException;
import exceptions.command.InvalidCommandException;
import exceptions.command.UnsuccessfulCommandException;
import exceptions.user.NotLoggedInException;
import cryptowallet.CryptoWalletService;
import user.User;

import java.nio.channels.SelectionKey;

public class DepositMoneyCommand implements Command {

    private final double amount;
    private final SelectionKey selectionKey;
    private final CryptoWalletService cryptoWalletService;

    private static final String SUCCESSFUL_MESSAGE = "You have successfully made a deposit of %f";

    public DepositMoneyCommand(String[] args, CryptoWalletService cryptoWalletService, SelectionKey selectionKey)
        throws IncorrectArgumentsCountException, InvalidCommandException {
        if (args == null) {
            throw new IllegalArgumentException("args in DepositMoney command cannot be null reference!");
        }
        if (cryptoWalletService == null) {
            throw new IllegalArgumentException("cryptoWalletService cannot be null reference!");
        }
        if (selectionKey == null) {
            throw new IllegalArgumentException("selectionKey cannot be null reference!");
        }
        if (args.length != 1) {
            throw new IncorrectArgumentsCountException("Deposit command should contain exactly one specific argument!");
        }
        try {
            this.amount = Double.parseDouble(args[0]);
            if (Double.compare(this.amount, 0d) <= 0) {
                throw new IllegalArgumentException("The amount in the deposit-money command cannot be below 0.00 USD");
            }
            this.selectionKey = selectionKey;
            this.cryptoWalletService = cryptoWalletService;
        } catch (IllegalArgumentException e) {
            throw new InvalidCommandException("The amount in the deposit-money command is not in an appropriate format",
                e);
        }
    }

    @Override
    public String execute() throws UnsuccessfulCommandException {
        try {
            User user = (User) selectionKey.attachment();
            if (user == null) {
                throw new NotLoggedInException("Depositing money cannot happen before logging in!");
            }
            cryptoWalletService.depositMoneyInWallet(amount, user.cryptoWallet());
            return String.format(SUCCESSFUL_MESSAGE, amount);
        } catch (NotLoggedInException e) {
            throw new UnsuccessfulCommandException("DepositMoney command is unsuccessful! " + e.getMessage(), e);
        }
    }
}
