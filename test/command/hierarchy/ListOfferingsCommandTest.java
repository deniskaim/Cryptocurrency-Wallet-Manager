package command.hierarchy;

import exceptions.command.IncorrectArgumentsCountException;
import exceptions.command.UnsuccessfulCommandException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import service.cryptowallet.CryptoWalletService;
import service.cryptowallet.Offering;
import user.User;

import java.nio.channels.SelectionKey;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

public class ListOfferingsCommandTest {

    private String[] args;
    private CryptoWalletService cryptoWalletService;
    private SelectionKey selectionKey;
    private User user;
    private ListOfferingsCommand command;

    @BeforeEach
    void setUp() throws IncorrectArgumentsCountException {
        args = new String[0];
        cryptoWalletService = Mockito.mock(CryptoWalletService.class);
        selectionKey = Mockito.mock(SelectionKey.class);
        user = Mockito.mock(User.class);

        command = new ListOfferingsCommand(args, cryptoWalletService, selectionKey);
    }

    @Test
    void testConstructorShouldThrowIllegalArgumentExceptionWhenArgsIsNull() {
        assertThrows(IllegalArgumentException.class,
            () -> new ListOfferingsCommand(null, cryptoWalletService, selectionKey),
            "An IllegalArgumentException is expected when args in ListOfferingsCommand is null reference!");
    }

    @Test
    void testConstructorShouldThrowIllegalArgumentExceptionWhenCryptoWalletServiceIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new ListOfferingsCommand(args, null, selectionKey),
            "An IllegalArgumentException is expected when cryptoWalletService in ListOfferingsCommand is null reference!");
    }

    @Test
    void testConstructorShouldThrowIllegalArgumentExceptionWhenSelectionKeyIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new ListOfferingsCommand(args, cryptoWalletService, null),
            "An IllegalArgumentException is expected when selectionKey in ListOfferingsCommand is null reference!");
    }

    @Test
    void testConstructorShouldThrowIncorrectArgumentsCountException() {
        assertThrows(IncorrectArgumentsCountException.class,
            () -> new ListOfferingsCommand(new String[] {"onlyOneString"}, cryptoWalletService, selectionKey),
            "An IncorrectArgumentsCountException is expected when ListOfferingsCommand contains one argument!");
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

        when(cryptoWalletService.listOfferings()).thenReturn(List.of(offering1, offering2));

        String result = command.execute();
        String expectedResult = "Available Cryptocurrencies:" + System.lineSeparator()
            + "BTC 90000.0" + System.lineSeparator() + "ETH 10000.0";

        assertEquals(expectedResult, result, "ListOffering does not return the correct string");
    }
}
