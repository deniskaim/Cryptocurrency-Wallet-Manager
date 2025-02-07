package command.pattern;

import cryptowallet.offers.CryptoCatalog;
import exceptions.command.IncorrectArgumentsCountException;
import exceptions.command.UnsuccessfulCommandException;
import exceptions.user.NotLoggedInException;
import cryptowallet.CryptoWalletService;
import cryptowallet.offers.Offering;
import user.User;

import java.nio.channels.SelectionKey;
import java.util.List;

public class ListOfferingsCommand implements Command {

    private final CryptoWalletService cryptoWalletService;
    private final SelectionKey selectionKey;

    private static final String SUCCESSFUL_MESSAGE = "Available Cryptocurrencies:" + System.lineSeparator();

    public ListOfferingsCommand(String[] args, CryptoWalletService cryptoWalletService, SelectionKey selectionKey)
        throws IncorrectArgumentsCountException {
        if (args == null) {
            throw new IllegalArgumentException(
                "args in ListOfferings command cannot be null reference!");
        }
        if (args.length != 0) {
            throw new IncorrectArgumentsCountException(
                "ListOfferings command should not contain arguments!");
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
    public String execute() throws UnsuccessfulCommandException {
        try {
            User user = (User) selectionKey.attachment();
            if (user == null) {
                throw new NotLoggedInException("Listing offerings cannot happen before logging in!");
            }
            CryptoCatalog cryptoCatalog = cryptoWalletService.getCryptoCatalogWithOfferings();
            return cryptoCatalog.toString();
        } catch (NotLoggedInException e) {
            throw new UnsuccessfulCommandException("ListOfferings command is unsuccessful! " + e.getMessage(), e);
        }
    }
}
