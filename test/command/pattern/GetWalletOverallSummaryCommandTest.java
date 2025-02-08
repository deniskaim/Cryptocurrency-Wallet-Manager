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
        when(cryptoWalletService.accumulateProfitLoss(cryptoWallet)).thenThrow(NoActiveInvestmentsException.class);

        assertThrows(UnsuccessfulCommandException.class, () -> command.execute(),
            "An UnsuccessfulCommandException is expected when there are no active investments in the account");
    }

    @Test
    void testExecuteShouldReturnProfitMessageWhenTotalProfitIsPositive()
        throws UnsuccessfulCommandException, InvalidAssetException, NoActiveInvestmentsException {
        when(selectionKey.attachment()).thenReturn(user);
        when(user.cryptoWallet()).thenReturn(cryptoWallet);
        when(cryptoWalletService.accumulateProfitLoss(cryptoWallet)).thenReturn(10.0);  // Печалба

        String result = command.execute();
        assertEquals("Your investments have grown! Current profit: 10.000000 USD", result);
    }

    @Test
    void testExecuteShouldReturnLossMessageWhenTotalProfitIsNegative()
        throws UnsuccessfulCommandException, InvalidAssetException, NoActiveInvestmentsException {
        when(selectionKey.attachment()).thenReturn(user);
        when(user.cryptoWallet()).thenReturn(cryptoWallet);
        when(cryptoWalletService.accumulateProfitLoss(cryptoWallet)).thenReturn(-10.0);  // Загуба

        String result = command.execute();
        assertEquals("Current loss: -10.000000 USD. Investing always carries risks! Be patient!", result);
    }

    @Test
    void testExecuteShouldReturnNeutralMessageWhenTotalProfitIsZero()
        throws UnsuccessfulCommandException, InvalidAssetException, NoActiveInvestmentsException {
        when(selectionKey.attachment()).thenReturn(user);
        when(user.cryptoWallet()).thenReturn(cryptoWallet);
        when(cryptoWalletService.accumulateProfitLoss(cryptoWallet)).thenReturn(0.0);  // Няма печалба или загуба

        String result = command.execute();
        assertEquals("No profit or loss at the moment. Your investments are safe!", result);
    }

}
