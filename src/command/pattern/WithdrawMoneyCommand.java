package command.pattern;

import exceptions.command.IncorrectArgumentsCountException;
import exceptions.command.InvalidCommandException;
import exceptions.command.UnsuccessfulCommandException;
import exceptions.user.NotLoggedInException;
import exceptions.wallet.InsufficientFundsException;
import user.User;

import java.nio.channels.SelectionKey;

public class WithdrawMoneyCommand implements Command {

    private final double amount;
    private final SelectionKey selectionKey;

    private static final String SUCCESSFUL_MESSAGE = "You have successfully withdrawn %f USD";

    public WithdrawMoneyCommand(String[] args, SelectionKey selectionKey)
        throws IncorrectArgumentsCountException, InvalidCommandException {
        if (args == null) {
            throw new IllegalArgumentException("args in WithdrawMoney command cannot be null reference!");
        }
        if (selectionKey == null) {
            throw new IllegalArgumentException("selectionKey cannot be null reference!");
        }
        if (args.length != 1) {
            throw new IncorrectArgumentsCountException(
                "Withdraw command should contain exactly one specific argument!");
        }
        try {
            this.amount = Double.parseDouble(args[0]);
            this.selectionKey = selectionKey;
        } catch (IllegalArgumentException e) {
            throw new InvalidCommandException(
                "The amount in the withdraw-money command is not in an appropriate format", e);
        }
        if (Double.compare(this.amount, 0d) <= 0) {
            throw new InvalidCommandException(
                "The amount in the withdraw-money command cannot be below or equal to 0.00 USD");
        }
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
            throw new UnsuccessfulCommandException("DepositMoney command is unsuccessful! " + e.getMessage(), e);
        }
    }
}
