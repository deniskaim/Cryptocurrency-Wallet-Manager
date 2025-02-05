package command.hierarchy;

import exceptions.InvalidAssetException;
import exceptions.command.IncorrectArgumentsCountException;
import exceptions.command.InvalidCommandException;
import exceptions.command.UnsuccessfulCommandException;
import exceptions.wallet.MissingInWalletAssetException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import service.cryptowallet.CryptoWalletService;
import user.CryptoWallet;
import user.User;

import java.nio.channels.SelectionKey;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

public class SellCommandTest {

    private String[] args;
    private CryptoWalletService cryptoWalletService;
    private SelectionKey selectionKey;
    private User user;
    private CryptoWallet cryptoWallet;

    private SellCommand sellCommand;

    @BeforeEach
    void setUp() throws IncorrectArgumentsCountException, InvalidCommandException {
        args = new String[] {"--offering=BTC"};
        cryptoWalletService = Mockito.mock(CryptoWalletService.class);
        selectionKey = Mockito.mock(SelectionKey.class);
        user = Mockito.mock(User.class);
        cryptoWallet = Mockito.mock(CryptoWallet.class);

        sellCommand = new SellCommand(args, cryptoWalletService, selectionKey);
    }

    @Test
    void testConstructorShouldThrowIllegalArgumentExceptionWhenArgsIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new SellCommand(null, cryptoWalletService, selectionKey),
            "An IllegalArgumentException is expected when args in SellCommand is null reference!");
    }

    @Test
    void testConstructorShouldThrowIllegalArgumentExceptionWhenCryptoWalletServiceIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new SellCommand(args, null, selectionKey),
            "An IllegalArgumentException is expected when cryptoWalletService in SellCommand is null reference!");
    }

    @Test
    void testConstructorShouldThrowIllegalArgumentExceptionWhenSelectionKeyIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new SellCommand(args, cryptoWalletService, null),
            "An IllegalArgumentException is expected when selectionKey in SellCommand is null reference!");
    }

    @Test
    void testConstructorShouldThrowIncorrectArgumentsCountException() {
        assertThrows(IncorrectArgumentsCountException.class,
            () -> new SellCommand(new String[] {"firstString", "secondString"}, cryptoWalletService, selectionKey),
            "An IncorrectArgumentsCountException is expected when SellCommand contains one argument!");
    }

    @Test
    void testConstructorShouldThrowInvalidCommandExceptionWhenOfferingCodeIsInvalid() {
        assertThrows(
            InvalidCommandException.class,
            () -> new SellCommand(new String[] {"--invalidParam=BTC"}, cryptoWalletService, selectionKey),
            "An InvalidCommandException is expected when the offering code string is invalid!");
    }

    @Test
    void testExecuteWhenUserIsNotLoggedIn() {
        when(selectionKey.attachment()).thenReturn(null);
        assertThrows(UnsuccessfulCommandException.class, () -> sellCommand.execute(),
            "An UnsuccessfulCommandException is expected when the user is not logged in!");
    }

    @Test
    void testExecuteWhenAssetIsNotInWallet() throws MissingInWalletAssetException, InvalidAssetException {
        when(selectionKey.attachment()).thenReturn(user);
        when(user.cryptoWallet()).thenReturn(cryptoWallet);
        when(cryptoWalletService.sellCrypto("BTC", cryptoWallet)).thenThrow(MissingInWalletAssetException.class);

        assertThrows(UnsuccessfulCommandException.class, () -> sellCommand.execute(),
            "An UnsuccessfulCommandException is expected when the asset cannot be found in the wallet!");
    }

    @Test
    void testExecuteWhenAssetIsInvalid() throws MissingInWalletAssetException, InvalidAssetException {
        when(selectionKey.attachment()).thenReturn(user);
        when(user.cryptoWallet()).thenReturn(cryptoWallet);
        when(cryptoWalletService.sellCrypto("BTC", cryptoWallet)).thenThrow(InvalidAssetException.class);

        assertThrows(UnsuccessfulCommandException.class, () -> sellCommand.execute(),
            "An UnsuccessfulCommandException is expected when the asset is invalid!");
    }

    @Test
    void testExecute() throws MissingInWalletAssetException, InvalidAssetException, UnsuccessfulCommandException {
        when(selectionKey.attachment()).thenReturn(user);
        when(user.cryptoWallet()).thenReturn(cryptoWallet);
        when(cryptoWalletService.sellCrypto("BTC", cryptoWallet)).thenReturn(10d);

        String result = sellCommand.execute();
        String expectedResult = "You have successfully sold your BTC for 10.000000 USD";

        assertEquals(expectedResult, result, "execute() doesn't return the correct string!");
    }
}
