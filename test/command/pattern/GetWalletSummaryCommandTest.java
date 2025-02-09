package command.pattern;

import cryptowallet.summary.CryptoWalletSummary;
import exceptions.command.UnsuccessfulCommandException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import cryptowallet.CryptoWallet;
import user.User;

import java.nio.channels.SelectionKey;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

public class GetWalletSummaryCommandTest {

    private SelectionKey selectionKey;
    private CryptoWallet cryptoWallet;
    private User user;

    private GetWalletSummaryCommand command;

    @BeforeEach
    void setUp() {
        selectionKey = Mockito.mock(SelectionKey.class);
        cryptoWallet = Mockito.mock(CryptoWallet.class);
        user = Mockito.mock(User.class);
        command = new GetWalletSummaryCommand(selectionKey);
    }

    @Test
    void testConstructorShouldThrowIllegalArgumentExceptionWhenSelectionKeyIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new GetWalletSummaryCommand(null),
            "An IllegalArgumentException is expected when selectionKey in GetWalletSummaryCommand is null reference!");
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
        CryptoWalletSummary summaryMock = Mockito.mock(CryptoWalletSummary.class);

        when(selectionKey.attachment()).thenReturn(user);
        when(user.cryptoWallet()).thenReturn(cryptoWallet);
        when(cryptoWallet.getSummary()).thenReturn(summaryMock);
        when(summaryMock.toString()).thenReturn(exampleSummary);

        String result = command.execute();
        assertEquals(exampleSummary, result, "GetWalletSummaryCommand doesn't return the correct string!");
    }

}
