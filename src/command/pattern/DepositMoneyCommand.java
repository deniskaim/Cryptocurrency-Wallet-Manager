package command.pattern;

import exceptions.command.UnsuccessfulCommandException;
import exceptions.user.NotLoggedInException;
import user.User;

import java.nio.channels.SelectionKey;

public class DepositMoneyCommand implements Command {

    private final double amount;
    private final SelectionKey selectionKey;

    private static final String SUCCESSFUL_MESSAGE = "You have successfully made a deposit of %f USD";

    public DepositMoneyCommand(double amount, SelectionKey selectionKey) {
        if (selectionKey == null) {
            throw new IllegalArgumentException("selectionKey cannot be null reference!");
        }
        if (Double.compare(amount, 0d) <= 0) {
            throw new IllegalArgumentException(
                "The amount in the deposit-money command cannot be below or equal to 0.00 USD");
        }

        this.amount = amount;
        this.selectionKey = selectionKey;
    }

    @Override
    public String execute() throws UnsuccessfulCommandException {
        try {
            User user = (User) selectionKey.attachment();
            if (user == null) {
                throw new NotLoggedInException("Depositing money cannot happen before logging in!");
            }
            user.cryptoWallet().depositMoney(amount);
            return String.format(SUCCESSFUL_MESSAGE, amount);
        } catch (NotLoggedInException e) {
            throw new UnsuccessfulCommandException("DepositMoney command is unsuccessful! " + e.getMessage(), e);
        }
    }
}
