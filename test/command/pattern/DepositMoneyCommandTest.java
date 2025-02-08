package command.pattern;

import exceptions.command.UnsuccessfulCommandException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import cryptowallet.CryptoWallet;
import user.User;

import java.nio.channels.SelectionKey;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DepositMoneyCommandTest {

    private double amount;
    private SelectionKey selectionKey;
    private User user;
    private CryptoWallet cryptoWallet;

    private DepositMoneyCommand depositMoneyCommand;

    @BeforeEach
    void setUp() {
        amount = 10;
        selectionKey = Mockito.mock(SelectionKey.class);
        user = Mockito.mock(User.class);
        cryptoWallet = Mockito.mock(CryptoWallet.class);

        depositMoneyCommand = new DepositMoneyCommand(amount, selectionKey);
    }

    @Test
    void testConstructorShouldThrowIllegalArgumentExceptionWhenSelectionKeyIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new DepositMoneyCommand(amount, null),
            "An IllegalArgumentException is expected when selectionKey in DepositMoneyCommand is null reference!");
    }

    @Test
    void testConstructorShouldThrowInvalidCommandExceptionWhenAmountIsZero() {
        assertThrows(IllegalArgumentException.class,
            () -> new DepositMoneyCommand(0, selectionKey),
            "An InvalidCommandException is expected when the amount is equal to 0!");
    }

    @Test
    void testConstructorShouldThrowInvalidCommandExceptionWhenAmountIsNegative() {
        assertThrows(IllegalArgumentException.class,
            () -> new DepositMoneyCommand(-10, selectionKey),
            "An InvalidCommandException is expected when the amount is below 0!");
    }

    @Test
    void testExecuteWhenUserIsNotLoggedIn() {
        when(selectionKey.attachment()).thenReturn(null);
        assertThrows(UnsuccessfulCommandException.class, () -> depositMoneyCommand.execute(),
            "An UnsuccessfulCommandException is expected when the user is not logged in!");
    }

    @Test
    void testExecute() throws UnsuccessfulCommandException {
        when(selectionKey.attachment()).thenReturn(user);
        when(user.cryptoWallet()).thenReturn(cryptoWallet);
        doNothing().when(cryptoWallet).depositMoney(10);

        assertDoesNotThrow(() -> depositMoneyCommand.execute(),
            "depositMoneyCommand should not throw an exception when all params are valid");
        verify(cryptoWallet, times(1)).depositMoney(10);
    }
}
