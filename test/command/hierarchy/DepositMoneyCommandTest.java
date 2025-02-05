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
            "args in DepositMoneyCommand cannot be null reference!");
    }

    @Test
    void testConstructorShouldThrowIllegalArgumentExceptionWhenCryptoWalletServiceIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new DepositMoneyCommand(args, null, selectionKey),
            "cryptoWalletService in DepositMoneyCommand cannot be null reference!");
    }

    @Test
    void testConstructorShouldThrowIllegalArgumentExceptionWhenSelectionKeyIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new DepositMoneyCommand(args, cryptoWalletService, null),
            "selectionKey in DepositMoneyCommand cannot be null reference!");
    }

    @Test
    void testConstructorShouldThrowIncorrectArgumentsCountException() {
        assertThrows(IncorrectArgumentsCountException.class,
            () -> new DepositMoneyCommand(new String[] {"10", "randomExtraString"}, cryptoWalletService, selectionKey),
            "DepositMoneyCommand should contain exactly one specific argument!");
    }

    @Test
    void testConstructorShouldThrowInvalidCommandExceptionWhenAmountIsNotANumber() {
        assertThrows(InvalidCommandException.class,
            () -> new DepositMoneyCommand(new String[] {"abc"}, cryptoWalletService, selectionKey),
            "The amount in the DepositMoneyCommand is not in an appropriate format!");
    }

    @Test
    void testConstructorShouldThrowInvalidCommandExceptionWhenAmountIsZero() {
        assertThrows(InvalidCommandException.class,
            () -> new DepositMoneyCommand(new String[] {"0"}, cryptoWalletService, selectionKey),
            "The amount in the DepositMoneyCommand cannot be equal to 0!");
    }

    @Test
    void testConstructorShouldThrowInvalidCommandExceptionWhenAmountIsNegative() {
        assertThrows(InvalidCommandException.class,
            () -> new DepositMoneyCommand(new String[] {"-10"}, cryptoWalletService, selectionKey),
            "The amount in the DepositMoneyCommand cannot be below 0!");
    }

    @Test
    void testExecuteWhenUserIsNotLoggedIn() {
        when(selectionKey.attachment()).thenReturn(null);
        assertThrows(UnsuccessfulCommandException.class, () -> depositMoneyCommand.execute(),
            "Depositing cannot happen before logging in!");
    }

    @Test
    void testExecute() throws UnsuccessfulCommandException {
        when(selectionKey.attachment()).thenReturn(user);
        when(user.cryptoWallet()).thenReturn(cryptoWallet);

        assertDoesNotThrow(() -> depositMoneyCommand.execute(),
            "depositMoneyCommand should not throw an exception when all params are valid");
        verify(cryptoWalletService, times(1)).depositMoneyInWallet(10, cryptoWallet);
    }


}
