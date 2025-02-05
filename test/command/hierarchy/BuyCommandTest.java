package command.hierarchy;

import exceptions.InvalidAssetException;
import exceptions.command.IncorrectArgumentsCountException;
import exceptions.command.InvalidCommandException;
import exceptions.command.UnsuccessfulCommandException;
import exceptions.wallet.InsufficientFundsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import service.cryptowallet.CryptoWalletService;
import user.CryptoWallet;
import user.User;

import java.nio.channels.SelectionKey;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

public class BuyCommandTest {

    private String[] args;
    private CryptoWalletService cryptoWalletService;
    private SelectionKey selectionKey;
    private User user;
    private CryptoWallet cryptoWallet;

    private BuyCommand buyCommand;

    @BeforeEach
    void setUp() throws IncorrectArgumentsCountException, InvalidCommandException {
        args = new String[] {"--offering=BTC", "--money=10"};
        cryptoWalletService = Mockito.mock(CryptoWalletService.class);
        selectionKey = Mockito.mock(SelectionKey.class);
        user = Mockito.mock(User.class);
        cryptoWallet = Mockito.mock(CryptoWallet.class);

        buyCommand = new BuyCommand(args, cryptoWalletService, selectionKey);
    }

    @Test
    void testConstructorShouldThrowIllegalArgumentExceptionWhenArgsIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new BuyCommand(null, cryptoWalletService, selectionKey),
            "args in BuyCommand cannot be null reference!");
    }

    @Test
    void testConstructorShouldThrowIllegalArgumentExceptionWhenCryptoWalletServiceIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new BuyCommand(args, null, selectionKey),
            "cryptoWalletService in BuyCommand cannot be null reference!");
    }

    @Test
    void testConstructorShouldThrowIllegalArgumentExceptionWhenSelectionKeyIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new BuyCommand(args, cryptoWalletService, null),
            "selectionKey in BuyCommand cannot be null reference!");
    }

    @Test
    void testConstructorShouldThrowIncorrectArgumentsCountException() {
        assertThrows(IncorrectArgumentsCountException.class,
            () -> new BuyCommand(new String[] {"onlyOneString"}, cryptoWalletService, selectionKey),
            "Buy command should contain exactly two specific arguments!");
    }

    @Test
    void testConstructorShouldThrowInvalidCommandExceptionWhenOfferingCodeIsInvalid() {
        assertThrows(
            InvalidCommandException.class,
            () -> new BuyCommand(new String[] {"--invalidParam=BTC", "--money=10"}, cryptoWalletService, selectionKey),
            "Offering code string is invalid!");
    }

    @Test
    void testConstructorShouldThrowInvalidCommandExceptionWhenMoneyCodeIsInvalid() {
        assertThrows(
            InvalidCommandException.class,
            () -> new BuyCommand(new String[] {"--offering=BTC", "--invalidParam=10"}, cryptoWalletService,
                selectionKey),
            "Money string is invalid!");
    }

    @Test
    void testConstructorShouldThrowInvalidCommandExceptionWhenAmountIsNotANumber() {
        assertThrows(
            InvalidCommandException.class,
            () -> new BuyCommand(new String[] {"--offering=BTC", "--money=abc"}, cryptoWalletService, selectionKey),
            "The amount in the buy-money command is not in an appropriate format!");
    }

    @Test
    void testConstructorShouldThrowInvalidCommandExceptionWhenAmountIsNegative() {
        assertThrows(InvalidCommandException.class,
            () -> new BuyCommand(new String[] {"--offering=BTC", "--money=-10"}, cryptoWalletService, selectionKey),
            "The amount in the buy-money command cannot be below 0!");
    }

    @Test
    void testExecuteWhenUserIsNotLoggedIn() {
        when(selectionKey.attachment()).thenReturn(null);
        assertThrows(UnsuccessfulCommandException.class, () -> buyCommand.execute(),
            "Buying cannot happen before logging in!");
    }

    @Test
    void testExecuteWhenInsufficientFunds() throws InsufficientFundsException, InvalidAssetException {
        when(selectionKey.attachment()).thenReturn(user);
        when(user.cryptoWallet()).thenReturn(cryptoWallet);
        when(cryptoWalletService.buyCrypto(10, "BTC", cryptoWallet)).thenThrow(InsufficientFundsException.class);

        assertThrows(UnsuccessfulCommandException.class, () -> buyCommand.execute(), "Insufficient funds!");
    }

    @Test
    void testExecuteWhenInvalidAsset() throws InsufficientFundsException, InvalidAssetException {
        when(selectionKey.attachment()).thenReturn(user);
        when(user.cryptoWallet()).thenReturn(cryptoWallet);
        when(cryptoWalletService.buyCrypto(10, "BTC", cryptoWallet)).thenThrow(InvalidAssetException.class);

        assertThrows(UnsuccessfulCommandException.class, () -> buyCommand.execute(), "Invalid asset!");
    }

    @Test
    void testExecute() throws InsufficientFundsException, InvalidAssetException, UnsuccessfulCommandException {
        when(selectionKey.attachment()).thenReturn(user);
        when(user.cryptoWallet()).thenReturn(cryptoWallet);
        when(cryptoWalletService.buyCrypto(10, "BTC", cryptoWallet)).thenReturn(0.002);

        String result = buyCommand.execute();
        assertDoesNotThrow(() -> buyCommand.execute());
        assertEquals("You have successfully bought 0.002000 of BTC", result);
    }
}
