package command.pattern;

import exceptions.command.UnsuccessfulCommandException;
import exceptions.user.NotLoggedInException;
import exceptions.wallet.InsufficientFundsException;
import user.User;

import java.nio.channels.SelectionKey;

public class WithdrawMoneyCommand implements Command {

    private final double amount;
    private final SelectionKey selectionKey;

    private static final String SUCCESSFUL_MESSAGE = "You have successfully withdrawn %f USD";

    public WithdrawMoneyCommand(double amount, SelectionKey selectionKey) {
        if (selectionKey == null) {
            throw new IllegalArgumentException("selectionKey cannot be null reference!");
        }
        if (Double.compare(amount, 0d) <= 0) {
            throw new IllegalArgumentException(
                "The amount in the withdraw-money command cannot be below or equal to 0.00 USD!");
        }
        this.amount = amount;
        this.selectionKey = selectionKey;
    }

    @Override
    public String execute() throws UnsuccessfulCommandException {
        try {
            User user = (User) selectionKey.attachment();
            if (user == null) {
                throw new NotLoggedInException("Withdrawing money cannot happen before logging in!");
            }
            user.cryptoWallet().withdrawMoney(amount);
            return String.format(SUCCESSFUL_MESSAGE, amount);
        } catch (NotLoggedInException | InsufficientFundsException e) {
            throw new UnsuccessfulCommandException("Withdrawing command is unsuccessful! " + e.getMessage(), e);
        }
    }
}
