package command.hierarchy;

import exceptions.command.IncorrectArgumentsCountException;
import exceptions.command.UnsuccessfulCommandException;
import exceptions.user.NotLoggedInException;
import user.User;

import java.nio.channels.SelectionKey;

public class GetWalletSummaryCommand implements Command {

    private final SelectionKey selectionKey;

    public GetWalletSummaryCommand(String[] args, SelectionKey selectionKey) throws IncorrectArgumentsCountException {
        if (args == null) {
            throw new IllegalArgumentException(
                "args in GetWalletSummary command cannot be null reference!");
        }
        if (args.length != 0) {
            throw new IncorrectArgumentsCountException(
                "GetWalletSummary command should not contain arguments!");
        }
        if (selectionKey == null) {
            throw new IllegalArgumentException("selectionKey cannot be null reference!");
        }

        this.selectionKey = selectionKey;
    }

    @Override
    public String execute() throws UnsuccessfulCommandException {
        try {
            User user = (User) selectionKey.attachment();
            if (user == null) {
                throw new NotLoggedInException("Get-wallet-summary cannot happen before logging in!");
            }
            return user.cryptoWallet().getSummary();
        } catch (NotLoggedInException e) {
            throw new UnsuccessfulCommandException("GetWalletSummary command is unsuccessful! " + e.getMessage(), e);
        }

    }

}
