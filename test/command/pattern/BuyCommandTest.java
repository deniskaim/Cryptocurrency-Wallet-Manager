package command.pattern;

import exceptions.InvalidAssetException;
import exceptions.command.InvalidCommandException;
import exceptions.command.UnsuccessfulCommandException;
import exceptions.wallet.InsufficientFundsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import cryptowallet.CryptoWalletService;
import cryptowallet.CryptoWallet;
import user.User;

import java.nio.channels.SelectionKey;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

public class BuyCommandTest {

    private String assetID;
    private double amount;
    private CryptoWalletService cryptoWalletService;
    private SelectionKey selectionKey;
    private User user;
    private CryptoWallet cryptoWallet;

    private BuyCommand buyCommand;

    @BeforeEach
    void setUp() {
        assetID = "BTC";
        amount = 10d;

        cryptoWalletService = Mockito.mock(CryptoWalletService.class);
        selectionKey = Mockito.mock(SelectionKey.class);
        user = Mockito.mock(User.class);
        cryptoWallet = Mockito.mock(CryptoWallet.class);

        buyCommand = new BuyCommand(assetID, amount, cryptoWalletService, selectionKey);
    }

    @Test
    void testConstructorShouldThrowIllegalArgumentExceptionWhenAssetIDIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new BuyCommand(null, 10d, cryptoWalletService, selectionKey),
            "An IllegalArgumentException is expected when assetID in BuyCommand is null reference!");
    }

    @Test
    void testConstructorShouldThrowIllegalArgumentExceptionWhenCryptoWalletServiceIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new BuyCommand(assetID, amount, null, selectionKey),
            "An IllegalArgumentException is expected when cryptoWalletService in BuyCommand is null reference!");
    }

    @Test
    void testConstructorShouldThrowIllegalArgumentExceptionWhenSelectionKeyIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new BuyCommand(assetID, amount, cryptoWalletService, null),
            "An IllegalArgumentException is expected when selectionKey in BuyCommand is null reference!");
    }

    @Test
    void testConstructorShouldThrowInvalidCommandExceptionWhenAmountIsZero() {
        assertThrows(IllegalArgumentException.class,
            () -> new BuyCommand(assetID, 0, cryptoWalletService, selectionKey),
            "An InvalidCommandException is expected when the amount in the BuyCommand is equal to 0!");
    }

    @Test
    void testConstructorShouldThrowInvalidCommandExceptionWhenAmountIsNegative() {
        assertThrows(IllegalArgumentException.class,
            () -> new BuyCommand(assetID, -10, cryptoWalletService, selectionKey),
            "An InvalidCommandException is expected when the amount in the BuyCommand is below 0!");
    }

    @Test
    void testExecuteWhenUserIsNotLoggedIn() {
        when(selectionKey.attachment()).thenReturn(null);
        assertThrows(UnsuccessfulCommandException.class, () -> buyCommand.execute(),
            "An UnsuccessfulCommandException is expected when the user is not logged in!");
    }

    @Test
    void testExecuteWhenInsufficientFunds() throws InsufficientFundsException, InvalidAssetException {
        when(selectionKey.attachment()).thenReturn(user);
        when(user.cryptoWallet()).thenReturn(cryptoWallet);
        when(cryptoWalletService.buyCrypto(10, "BTC", cryptoWallet)).thenThrow(InsufficientFundsException.class);

        assertThrows(UnsuccessfulCommandException.class, () -> buyCommand.execute(),
            "An UnsuccessfulCommandException is expected when the funds are insufficient!");
    }

    @Test
    void testExecuteWhenInvalidAsset() throws InsufficientFundsException, InvalidAssetException {
        when(selectionKey.attachment()).thenReturn(user);
        when(user.cryptoWallet()).thenReturn(cryptoWallet);
        when(cryptoWalletService.buyCrypto(10, "BTC", cryptoWallet)).thenThrow(InvalidAssetException.class);

        assertThrows(UnsuccessfulCommandException.class, () -> buyCommand.execute(),
            "An UnsuccessfulCommandException is expected when the asset is invalid!");
    }

    @Test
    void testExecute() throws InsufficientFundsException, InvalidAssetException, UnsuccessfulCommandException {
        when(selectionKey.attachment()).thenReturn(user);
        when(user.cryptoWallet()).thenReturn(cryptoWallet);
        when(cryptoWalletService.buyCrypto(10, "BTC", cryptoWallet)).thenReturn(0.002);

        String result = buyCommand.execute();
        assertDoesNotThrow(() -> buyCommand.execute(),
            "execute() should not throw an Exception when all parameters are valid!");
        assertEquals("You have successfully bought 0.002000 of BTC", result);
    }
}
