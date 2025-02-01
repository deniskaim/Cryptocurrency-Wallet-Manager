package server.command.hierarchy;

import exceptions.NotLoggedInException;
import server.system.User;

import java.nio.channels.SelectionKey;

public class DepositMoneyCommand implements Command {

    private final double amount;
    private final SelectionKey selectionKey;

    private static final String SUCCESSFUL_MESSAGE = "You have successfully made a deposit of %f";

    public DepositMoneyCommand(String[] args, SelectionKey selectionKey) {
        if (args == null || args.length != 1) {
            throw new IllegalArgumentException("DepositMoney should be include only one argument - the amount");
        }
        if (selectionKey == null) {
            throw new IllegalArgumentException("selectionKey cannot be null!");
        }

        try {
            this.amount = Double.parseDouble(args[0]);
            this.selectionKey = selectionKey;
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
        user.cryptoWallet().depositMoney(amount);
        return String.format(SUCCESSFUL_MESSAGE, amount);
    }
}
