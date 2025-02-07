package command.pattern;

import cryptowallet.CryptoWallet;
import exceptions.command.IncorrectArgumentsCountException;
import exceptions.command.InvalidCommandException;
import exceptions.command.UnsuccessfulCommandException;
import exceptions.wallet.InsufficientFundsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import user.User;

import java.nio.channels.SelectionKey;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class WithdrawMoneyCommandTest {

    private String[] args;
    private SelectionKey selectionKey;
    private User user;
    private CryptoWallet cryptoWallet;

    private WithdrawMoneyCommand withdrawMoneyCommand;

    @BeforeEach
    void setUp() throws IncorrectArgumentsCountException, InvalidCommandException {
        args = new String[] {"10"};
        selectionKey = Mockito.mock(SelectionKey.class);
        user = Mockito.mock(User.class);
        cryptoWallet = Mockito.mock(CryptoWallet.class);

        withdrawMoneyCommand = new WithdrawMoneyCommand(args, selectionKey);
    }

    @Test
    void testConstructorShouldThrowIllegalArgumentExceptionWhenArgsIsNull() {
        assertThrows(IllegalArgumentException.class,
            () -> new WithdrawMoneyCommand(null, selectionKey),
            "An IllegalArgumentException is expected when args in WithdrawMoneyCommand is null reference!");
    }

    @Test
    void testConstructorShouldThrowIllegalArgumentExceptionWhenSelectionKeyIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new WithdrawMoneyCommand(args, null),
            "An IllegalArgumentException is expected when selectionKey in WithdrawMoneyCommand is null reference!");
    }

    @Test
    void testConstructorShouldThrowIncorrectArgumentsCountException() {
        assertThrows(IncorrectArgumentsCountException.class,
            () -> new WithdrawMoneyCommand(new String[] {"10", "randomExtraString"}, selectionKey),
            "An IncorrectArgumentsCountException is expected when args consists of two strings");
    }

    @Test
    void testConstructorShouldThrowInvalidCommandExceptionWhenAmountIsNotANumber() {
        assertThrows(InvalidCommandException.class,
            () -> new WithdrawMoneyCommand(new String[] {"abc"}, selectionKey),
            "An InvalidCommandException is expected when the amount is not in an appropriate format!");
    }

    @Test
    void testConstructorShouldThrowInvalidCommandExceptionWhenAmountIsZero() {
        assertThrows(InvalidCommandException.class,
            () -> new WithdrawMoneyCommand(new String[] {"0"}, selectionKey),
            "An InvalidCommandException is expected when the amount is equal to 0!");
    }

    @Test
    void testConstructorShouldThrowInvalidCommandExceptionWhenAmountIsNegative() {
        assertThrows(InvalidCommandException.class,
            () -> new WithdrawMoneyCommand(new String[] {"-10"}, selectionKey),
            "An InvalidCommandException is expected when the amount is below 0!");
    }

    @Test
    void testExecuteWhenUserIsNotLoggedIn() {
        when(selectionKey.attachment()).thenReturn(null);
        assertThrows(UnsuccessfulCommandException.class, () -> withdrawMoneyCommand.execute(),
            "An UnsuccessfulCommandException is expected when the user is not logged in!");
    }

    @Test
    void testExecuteWhenInsufficientFunds() throws InsufficientFundsException {
        when(selectionKey.attachment()).thenReturn(user);
        when(user.cryptoWallet()).thenReturn(cryptoWallet);
        doThrow(InsufficientFundsException.class).when(cryptoWallet).withdrawMoney(10);

        assertThrows(UnsuccessfulCommandException.class, () -> withdrawMoneyCommand.execute(),
            "An UnsuccessfulCommandException is expected when the funds are insufficient!");
    }

    @Test
    void testExecute() throws UnsuccessfulCommandException, InsufficientFundsException {
        when(selectionKey.attachment()).thenReturn(user);
        when(user.cryptoWallet()).thenReturn(cryptoWallet);
        doNothing().when(cryptoWallet).withdrawMoney(10);

        assertDoesNotThrow(() -> withdrawMoneyCommand.execute(),
            "withdrawMoneyCommand should not throw an exception when all params are valid");
        verify(cryptoWallet, times(1)).withdrawMoney(10);
    }
}
