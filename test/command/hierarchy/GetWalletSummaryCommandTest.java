package command.hierarchy;

import exceptions.command.IncorrectArgumentsCountException;
import exceptions.command.UnsuccessfulCommandException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import user.CryptoWallet;
import user.User;

import java.nio.channels.SelectionKey;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

public class GetWalletSummaryCommandTest {

    private String[] args;
    private SelectionKey selectionKey;
    private CryptoWallet cryptoWallet;
    private User user;

    private GetWalletSummaryCommand command;

    @BeforeEach
    void setUp() throws IncorrectArgumentsCountException {
        args = new String[0];
        selectionKey = Mockito.mock(SelectionKey.class);
        cryptoWallet = Mockito.mock(CryptoWallet.class);
        user = Mockito.mock(User.class);
        command = new GetWalletSummaryCommand(args, selectionKey);
    }

    @Test
    void testConstructorShouldThrowIllegalArgumentExceptionWhenArgsIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new GetWalletSummaryCommand(null, selectionKey),
            "An IllegalArgumentException is expected when args in GetWalletSummaryCommand is null reference!");
    }

    @Test
    void testConstructorShouldThrowIllegalArgumentExceptionWhenSelectionKeyIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new GetWalletSummaryCommand(args, null),
            "An IllegalArgumentException is expected when selectionKey in GetWalletSummaryCommand is null reference!");
    }

    @Test
    void testConstructorShouldThrowIncorrectArgumentsCountException() {
        assertThrows(IncorrectArgumentsCountException.class,
            () -> new GetWalletSummaryCommand(new String[] {"invalidParam"}, selectionKey),
            "An IncorrectArgumentsCountException is expected when GetWalletSummaryCommand contains an argument!");
    }

    @Test
    void testExecuteWhenUserIsNotLoggedIn() {
        when(selectionKey.attachment()).thenReturn(null);
        assertThrows(UnsuccessfulCommandException.class, () -> command.execute(),
            "An UnsuccessfulCommandException is expected when the user is not logged in!");
    }

    @Test
    void testExecute() throws UnsuccessfulCommandException {
        String exampleSummary = "Wallet summary check!";

        when(selectionKey.attachment()).thenReturn(user);
        when(user.cryptoWallet()).thenReturn(cryptoWallet);
        when(cryptoWallet.getSummary()).thenReturn(exampleSummary);

        String result = command.execute();
        assertEquals(exampleSummary, result);
    }

}
