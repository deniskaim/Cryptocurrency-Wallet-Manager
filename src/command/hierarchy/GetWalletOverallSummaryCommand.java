package command.hierarchy;

import exceptions.NoActiveInvestmentsException;
import exceptions.NotLoggedInException;
import service.cryptowallet.CryptoWalletService;
import user.User;

import java.nio.channels.SelectionKey;

public class GetWalletOverallSummaryCommand implements Command {

    private final CryptoWalletService cryptoWalletService;
    private final SelectionKey selectionKey;

    private static final String PROFIT_MESSAGE = "Your investments have grown! Current profit: %f USD";
    private static final String LOSS_MESSAGE = "Current loss: %f USD. Investing always carries risks! Be patient!";
    private static final String NEUTRAL_MESSAGE = "No profit or loss at the moment. Your investments are safe!";

    public GetWalletOverallSummaryCommand(String[] args, CryptoWalletService cryptoWalletService,
                                          SelectionKey selectionKey) {
        if (args == null || args.length != 0) {
            throw new IllegalArgumentException(
                "args cannot be null reference and Get-Wallet-Overall-Summary command should not contain arguments!");
        }
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
    public String execute() throws NotLoggedInException, NoActiveInvestmentsException {
        User user = (User) selectionKey.attachment();
        if (user == null) {
            throw new NotLoggedInException("Get-wallet-overall-summary cannot happen before logging in!");
        }

        double totalProfitLoss = cryptoWalletService.accumulateProfitLoss(user.cryptoWallet());

        if (Double.compare(totalProfitLoss, 0d) > 0) {
            return String.format(PROFIT_MESSAGE, totalProfitLoss);
        } else if (Double.compare(totalProfitLoss, 0d) < 0) {
            return String.format(LOSS_MESSAGE, totalProfitLoss);
        } else {
            return NEUTRAL_MESSAGE;
        }
    }
}
