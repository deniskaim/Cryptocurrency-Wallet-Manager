package command.pattern;

import cryptowallet.summary.CryptoWalletOverallSummary;
import exceptions.InvalidAssetException;
import exceptions.command.UnsuccessfulCommandException;
import exceptions.wallet.NoActiveInvestmentsException;
import exceptions.user.NotLoggedInException;
import cryptowallet.CryptoWalletService;
import user.User;

import java.nio.channels.SelectionKey;

public class GetWalletOverallSummaryCommand implements Command {

    private final CryptoWalletService cryptoWalletService;
    private final SelectionKey selectionKey;

    public GetWalletOverallSummaryCommand(CryptoWalletService cryptoWalletService,
                                          SelectionKey selectionKey) {
        if (cryptoWalletService == null) {
            throw new IllegalArgumentException("cryptoWalletService cannot be null reference!");
        }
        if (selectionKey == null) {
            throw new IllegalArgumentException("selectionKey cannot be null reference!");
        }

        this.cryptoWalletService = cryptoWalletService;
        this.selectionKey = selectionKey;
    }

    @Override
    public String execute()
        throws UnsuccessfulCommandException {
        User user = (User) selectionKey.attachment();
        try {
            if (user == null) {
                throw new NotLoggedInException("Get-wallet-overall-summary cannot happen before logging in!");
            }
            CryptoWalletOverallSummary cryptoWalletOverallSummary =
                cryptoWalletService.getWalletOverallSummary(user.cryptoWallet());
            return cryptoWalletOverallSummary.toString();
        } catch (NotLoggedInException | NoActiveInvestmentsException | InvalidAssetException e) {
            throw new UnsuccessfulCommandException(
                "GetWalletOverallSummary command is not successful! " + e.getMessage(), e);
        }
    }
}
