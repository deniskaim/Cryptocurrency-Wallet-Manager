package command.hierarchy;

import exceptions.command.IncorrectArgumentsCountException;
import exceptions.command.InvalidCommandException;
import exceptions.command.UnsuccessfulCommandException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import service.cryptowallet.CryptoWalletService;
import user.CryptoWallet;
import user.User;

import java.nio.channels.SelectionKey;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DepositMoneyCommandTest {

    private String[] args;
    private CryptoWalletService cryptoWalletService;
    private SelectionKey selectionKey;
    private User user;
    private CryptoWallet cryptoWallet;

    private DepositMoneyCommand depositMoneyCommand;

    @BeforeEach
    void setUp() throws IncorrectArgumentsCountException, InvalidCommandException {
        args = new String[] {"10"};
        cryptoWalletService = Mockito.mock(CryptoWalletService.class);
        selectionKey = Mockito.mock(SelectionKey.class);
        user = Mockito.mock(User.class);
        cryptoWallet = Mockito.mock(CryptoWallet.class);

        depositMoneyCommand = new DepositMoneyCommand(args, cryptoWalletService, selectionKey);
    }

    @Test
    void testConstructorShouldThrowIllegalArgumentExceptionWhenArgsIsNull() {
        assertThrows(IllegalArgumentException.class,
            () -> new DepositMoneyCommand(null, cryptoWalletService, selectionKey),
            "An IllegalArgumentException is expected when args in DepositMoneyCommand is null reference!");
    }

    @Test
    void testConstructorShouldThrowIllegalArgumentExceptionWhenCryptoWalletServiceIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new DepositMoneyCommand(args, null, selectionKey),
            "An IllegalArgumentException is expected when cryptoWalletService in DepositMoneyCommand is null reference!");
    }

    @Test
    void testConstructorShouldThrowIllegalArgumentExceptionWhenSelectionKeyIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new DepositMoneyCommand(args, cryptoWalletService, null),
            "An IllegalArgumentException is expected when selectionKey in DepositMoneyCommand is null reference!");
    }

    @Test
    void testConstructorShouldThrowIncorrectArgumentsCountException() {
        assertThrows(IncorrectArgumentsCountException.class,
            () -> new DepositMoneyCommand(new String[] {"10", "randomExtraString"}, cryptoWalletService, selectionKey),
            "An IncorrectArgumentsCountException is expected when args consists of two strings");
    }

    @Test
    void testConstructorShouldThrowInvalidCommandExceptionWhenAmountIsNotANumber() {
        assertThrows(InvalidCommandException.class,
            () -> new DepositMoneyCommand(new String[] {"abc"}, cryptoWalletService, selectionKey),
            "An InvalidCommandException is expected when the amount is not in an appropriate format!");
    }

    @Test
    void testConstructorShouldThrowInvalidCommandExceptionWhenAmountIsZero() {
        assertThrows(InvalidCommandException.class,
            () -> new DepositMoneyCommand(new String[] {"0"}, cryptoWalletService, selectionKey),
            "An InvalidCommandException is expected when the amount is equal to 0!");
    }

    @Test
    void testConstructorShouldThrowInvalidCommandExceptionWhenAmountIsNegative() {
        assertThrows(InvalidCommandException.class,
            () -> new DepositMoneyCommand(new String[] {"-10"}, cryptoWalletService, selectionKey),
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
        doNothing().when(cryptoWalletService).depositMoneyInWallet(10, cryptoWallet);

        assertDoesNotThrow(() -> depositMoneyCommand.execute(),
            "depositMoneyCommand should not throw an exception when all params are valid");
        verify(cryptoWalletService, times(1)).depositMoneyInWallet(10, cryptoWallet);
    }


}
