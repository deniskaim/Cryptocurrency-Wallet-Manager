package command.pattern;

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

    private static final String PROFIT_MESSAGE = "Your investments have grown! Current profit: %f USD";
    private static final String LOSS_MESSAGE = "Current loss: %f USD. Investing always carries risks! Be patient!";
    private static final String NEUTRAL_MESSAGE = "No profit or loss at the moment. Your investments are safe!";

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
            double totalProfitLoss = cryptoWalletService.accumulateProfitLoss(user.cryptoWallet());
            return getCorrectResponse(totalProfitLoss);
        } catch (NotLoggedInException | NoActiveInvestmentsException | InvalidAssetException e) {
            throw new UnsuccessfulCommandException(
                "GetWalletOverallSummary command is not successful! " + e.getMessage(), e);
        }
    }

    private String getCorrectResponse(double totalProfitLoss) {
        if (Double.compare(totalProfitLoss, 0d) > 0) {
            return String.format(PROFIT_MESSAGE, totalProfitLoss);
        } else if (Double.compare(totalProfitLoss, 0d) < 0) {
            return String.format(LOSS_MESSAGE, totalProfitLoss);
        } else {
            return NEUTRAL_MESSAGE;
        }
    }
}
