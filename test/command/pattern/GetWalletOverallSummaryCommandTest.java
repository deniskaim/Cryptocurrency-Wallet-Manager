package command.pattern;

import exceptions.InvalidAssetException;
import exceptions.command.UnsuccessfulCommandException;
import exceptions.wallet.NoActiveInvestmentsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import cryptowallet.CryptoWalletService;
import cryptowallet.CryptoWallet;
import user.User;

import java.nio.channels.SelectionKey;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

public class GetWalletOverallSummaryCommandTest {

    private CryptoWalletService cryptoWalletService;
    private SelectionKey selectionKey;
    private User user;
    private CryptoWallet cryptoWallet;

    private GetWalletOverallSummaryCommand command;

    @BeforeEach
    void setUp() {
        cryptoWalletService = Mockito.mock(CryptoWalletService.class);
        selectionKey = Mockito.mock(SelectionKey.class);
        user = Mockito.mock(User.class);
        cryptoWallet = Mockito.mock(CryptoWallet.class);

        command = new GetWalletOverallSummaryCommand(cryptoWalletService, selectionKey);
    }

    @Test
    void testConstructorShouldThrowIllegalArgumentExceptionWhenCryptoWalletServiceIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new GetWalletOverallSummaryCommand(null, selectionKey),
            "cryptoWalletService in GetWalletOverallSummaryCommand cannot be null reference!");
    }

    @Test
    void testConstructorShouldThrowIllegalArgumentExceptionWhenSelectionKeyIsNull() {
        assertThrows(IllegalArgumentException.class,
            () -> new GetWalletOverallSummaryCommand(cryptoWalletService, null),
            "selectionKey in GetWalletOverallSummaryCommand cannot be null reference!");
    }

    @Test
    void testExecuteWhenUserIsNotLoggedIn() {
        when(selectionKey.attachment()).thenReturn(null);
        assertThrows(UnsuccessfulCommandException.class, () -> command.execute(),
            "An UnsuccessfulCommandException is expected when the user is not logged in!");
    }

    @Test
    void testExecuteWhenNoActiveInvestments() throws NoActiveInvestmentsException, InvalidAssetException {
        when(selectionKey.attachment()).thenReturn(user);
        when(user.cryptoWallet()).thenReturn(cryptoWallet);
        when(cryptoWalletService.getWalletOverallSummary(cryptoWallet)).thenThrow(NoActiveInvestmentsException.class);

        assertThrows(UnsuccessfulCommandException.class, () -> command.execute(),
            "An UnsuccessfulCommandException is expected when there are no active investments in the account");
    }

}
