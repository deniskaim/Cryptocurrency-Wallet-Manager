package command.hierarchy;

import exceptions.NotLoggedInException;
import user.User;

import java.nio.channels.SelectionKey;

public class GetWalletSummaryCommand implements Command {

    private final SelectionKey selectionKey;

    public GetWalletSummaryCommand(String[] args, SelectionKey selectionKey) {
        if (args == null || args.length != 0) {
            throw new IllegalArgumentException(
                "args cannot be null reference and Get-Wallet-Summary command should not contain arguments");
        }
        if (selectionKey == null) {
            throw new IllegalArgumentException("selectionKey cannot be null reference!");
        }

        this.selectionKey = selectionKey;
    }

    @Override
    public String execute() throws NotLoggedInException {
        User user = (User) selectionKey.attachment();
        if (user == null) {
            throw new NotLoggedInException("Get-wallet-summary cannot happen before logging in!");
        }
        return user.cryptoWallet().getSummary();
    }

}
