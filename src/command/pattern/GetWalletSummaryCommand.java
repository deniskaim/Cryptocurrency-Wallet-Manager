package command.pattern;

import cryptowallet.summary.CryptoWalletSummary;
import exceptions.command.UnsuccessfulCommandException;
import exceptions.user.NotLoggedInException;
import user.User;

import java.nio.channels.SelectionKey;

public class GetWalletSummaryCommand implements Command {

    private final SelectionKey selectionKey;

    public GetWalletSummaryCommand(SelectionKey selectionKey) {
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
            CryptoWalletSummary summary = user.cryptoWallet().getSummary();
            return summary.toString();
        } catch (NotLoggedInException e) {
            throw new UnsuccessfulCommandException("GetWalletSummary command is unsuccessful! " + e.getMessage(), e);
        }

    }

}
