package command.pattern;

import cryptowallet.offers.CryptoCatalog;
import exceptions.command.UnsuccessfulCommandException;
import exceptions.user.NotLoggedInException;
import cryptowallet.CryptoWalletService;
import user.User;

import java.nio.channels.SelectionKey;

public class ListOfferingsCommand implements Command {

    private final CryptoWalletService cryptoWalletService;
    private final SelectionKey selectionKey;

    public ListOfferingsCommand(CryptoWalletService cryptoWalletService, SelectionKey selectionKey) {
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
