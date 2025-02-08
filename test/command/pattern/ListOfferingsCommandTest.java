package command.pattern;

import cryptowallet.offers.CryptoCatalog;
import exceptions.command.UnsuccessfulCommandException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import cryptowallet.CryptoWalletService;
import cryptowallet.offers.Offering;
import user.User;

import java.nio.channels.SelectionKey;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

public class ListOfferingsCommandTest {

    private CryptoWalletService cryptoWalletService;
    private SelectionKey selectionKey;
    private User user;
    private ListOfferingsCommand command;

    @BeforeEach
    void setUp() {
        cryptoWalletService = Mockito.mock(CryptoWalletService.class);
        selectionKey = Mockito.mock(SelectionKey.class);
        user = Mockito.mock(User.class);

        command = new ListOfferingsCommand(cryptoWalletService, selectionKey);
    }

    @Test
    void testConstructorShouldThrowIllegalArgumentExceptionWhenCryptoWalletServiceIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new ListOfferingsCommand(null, selectionKey),
            "An IllegalArgumentException is expected when cryptoWalletService in ListOfferingsCommand is null reference!");
    }

    @Test
    void testConstructorShouldThrowIllegalArgumentExceptionWhenSelectionKeyIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new ListOfferingsCommand(cryptoWalletService, null),
            "An IllegalArgumentException is expected when selectionKey in ListOfferingsCommand is null reference!");
    }

    @Test
    void testExecuteWhenUserIsNotLoggedIn() {
        when(selectionKey.attachment()).thenReturn(null);
        assertThrows(UnsuccessfulCommandException.class, () -> command.execute(),
            "An UnsuccessfulCommandException is expected when the user is not logged in!");
    }

    @Test
    void testExecute() throws UnsuccessfulCommandException {
        when(selectionKey.attachment()).thenReturn(user);

        Offering offering1 = Mockito.mock(Offering.class);
        when(offering1.assetID()).thenReturn("BTC");
        when(offering1.price()).thenReturn(90000d);

        Offering offering2 = Mockito.mock(Offering.class);
        when(offering2.assetID()).thenReturn("ETH");
        when(offering2.price()).thenReturn(10000d);

        String expectedResult = "Available Cryptocurrencies:" + System.lineSeparator()
            + "BTC 90000.0" + System.lineSeparator() + "ETH 10000.0";

        CryptoCatalog catalogMock = Mockito.mock(CryptoCatalog.class);
        when(catalogMock.toString()).thenReturn(expectedResult);
        when(cryptoWalletService.getCryptoCatalogWithOfferings()).thenReturn(catalogMock);

        String result = command.execute();
        assertEquals(expectedResult, result, "ListOffering does not return the correct string");
    }
}
