package command;

import command.pattern.BuyCommand;
import command.pattern.Command;
import command.pattern.DepositMoneyCommand;
import command.pattern.DisconnectCommand;
import command.pattern.GetWalletOverallSummaryCommand;
import command.pattern.GetWalletSummaryCommand;
import command.pattern.HelpCommand;
import command.pattern.ListOfferingsCommand;
import command.pattern.LogInCommand;
import command.pattern.LogOutCommand;
import command.pattern.RegisterCommand;
import command.pattern.SellCommand;
import command.pattern.WithdrawMoneyCommand;
import exceptions.command.IncorrectArgumentsCountException;
import exceptions.command.InvalidCommandException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import user.UserAccountService;
import cryptowallet.CryptoWalletService;

import java.nio.channels.SelectionKey;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CommandFactoryTest {

    private CommandFactory commandFactory;
    private SelectionKey selectionKey;

    @BeforeEach
    void setUp() {
        UserAccountService userAccountService = Mockito.mock(UserAccountService.class);
        CryptoWalletService cryptoWalletService = Mockito.mock(CryptoWalletService.class);
        selectionKey = Mockito.mock(SelectionKey.class);
        commandFactory = CommandFactory.getInstance(userAccountService, cryptoWalletService);
    }

    @Test
    void testCreateCommandNullCommandMessage() {
        assertThrows(IllegalArgumentException.class, () -> commandFactory.createCommand(null, selectionKey),
            "An IllegalArgumentException is expected when commandMessage is null reference!");
    }

    @Test
    void testCreateCommandNullSelectionKey() {
        assertThrows(IllegalArgumentException.class, () -> commandFactory.createCommand("randomMessage", null),
            "An IllegalArgumentException is expected when selectionKey is null reference!");
    }

    @Test
    void testCreateCommandBuy() throws IncorrectArgumentsCountException, InvalidCommandException {
        String commandMessage = "$ buy --offering=BTC --money=10";

        Command command = commandFactory.createCommand(commandMessage, selectionKey);
        assertInstanceOf(BuyCommand.class, command, "A BuyCommand was expected!");
    }

    @Test
    void testCreateCommandBuyInvalidOfferingCode() {
        String commandMessage = "$ buy --wrongOffering=BTC --money=10";

        assertThrows(InvalidCommandException.class, () -> commandFactory.createCommand(commandMessage, selectionKey),
            "An InvalidCommandException is expected when the offering code is wrong!");
    }

    @Test
    void testCreateCommandBuyInvalidMoneyCode() {
        String commandMessage = "$ buy --offering=BTC --wrongMoney=10";

        assertThrows(InvalidCommandException.class, () -> commandFactory.createCommand(commandMessage, selectionKey),
            "An InvalidCommandException is expected when the money code is wrong!");
    }

    @Test
    void testCreateCommandBuyMoneyInappropriateFormat() {
        String commandMessage = "$ buy --offering=BTC --money=abc";

        assertThrows(InvalidCommandException.class, () -> commandFactory.createCommand(commandMessage, selectionKey),
            "An InvalidCommandException is expected when the money is in an inappropriate format!");
    }

    @Test
    void testCreateCommandBuyMoneyIsZero() {
        String commandMessage = "$ buy --offering=BTC --money=0";

        assertThrows(InvalidCommandException.class, () -> commandFactory.createCommand(commandMessage, selectionKey),
            "An InvalidCommandException is expected when the money is zero!");
    }

    @Test
    void testCreateCommandBuyMoneyIsNegative() {
        String commandMessage = "$ buy --offering=BTC --money=-10";

        assertThrows(InvalidCommandException.class, () -> commandFactory.createCommand(commandMessage, selectionKey),
            "An InvalidCommandException is expected when the money is negative!");
    }

    @Test
    void testCreateCommandDepositMoney() throws IncorrectArgumentsCountException, InvalidCommandException {
        String commandMessage = "$ deposit-money 10";

        Command command = commandFactory.createCommand(commandMessage, selectionKey);
        assertInstanceOf(DepositMoneyCommand.class, command, "A DepositMoneyCommand was expected!");
    }

    @Test
    void testCreateCommandDepositMoneyInappropriateFormat() {
        String commandMessage = "$ deposit-money abc";

        assertThrows(InvalidCommandException.class, () -> commandFactory.createCommand(commandMessage, selectionKey),
            "An InvalidCommandException is expected when the money is in an inappropriate format!");
    }

    @Test
    void testCreateCommandDepositMoneyWhenZero() {
        String commandMessage = "$ deposit-money 0";

        assertThrows(InvalidCommandException.class, () -> commandFactory.createCommand(commandMessage, selectionKey),
            "An InvalidCommandException is expected when the money is zero!");
    }

    @Test
    void testCreateCommandDepositMoneyWhenNegative() {
        String commandMessage = "$ deposit-money -10";

        assertThrows(InvalidCommandException.class, () -> commandFactory.createCommand(commandMessage, selectionKey),
            "An InvalidCommandException is expected when the money is negative!");
    }

    @Test
    void testCreateCommandDisconnect() throws IncorrectArgumentsCountException, InvalidCommandException {
        String commandMessage = "$ disconnect";

        Command command = commandFactory.createCommand(commandMessage, selectionKey);
        assertInstanceOf(DisconnectCommand.class, command, "A DisconnectCommand was expected!");
    }

    @Test
    void testCreateCommandGetWalletOverallSummary() throws IncorrectArgumentsCountException, InvalidCommandException {
        String commandMessage = "$ get-wallet-overall-summary";

        Command command = commandFactory.createCommand(commandMessage, selectionKey);
        assertInstanceOf(GetWalletOverallSummaryCommand.class, command,
            "A GetWalletOverallSummaryCommand was expected!");
    }

    @Test
    void testCreateCommandGetWalletSummary() throws IncorrectArgumentsCountException, InvalidCommandException {
        String commandMessage = "$ get-wallet-summary";

        Command command = commandFactory.createCommand(commandMessage, selectionKey);
        assertInstanceOf(GetWalletSummaryCommand.class, command,
            "A GetWalletSummaryCommand was expected!");
    }

    @Test
    void testCreateCommandHelp() throws IncorrectArgumentsCountException, InvalidCommandException {
        String commandMessage = "$ help";

        Command command = commandFactory.createCommand(commandMessage, selectionKey);
        assertInstanceOf(HelpCommand.class, command,
            "A HelpCommand was expected!");
    }

    @Test
    void testCreateCommandListOfferings() throws IncorrectArgumentsCountException, InvalidCommandException {
        String commandMessage = "$ list-offerings";

        Command command = commandFactory.createCommand(commandMessage, selectionKey);
        assertInstanceOf(ListOfferingsCommand.class, command,
            "A ListOfferingsCommand was expected!");
    }

    @Test
    void testCreateCommandLogIn() throws IncorrectArgumentsCountException, InvalidCommandException {
        String commandMessage = "$ login username password";

        Command command = commandFactory.createCommand(commandMessage, selectionKey);
        assertInstanceOf(LogInCommand.class, command,
            "A LogInCommand was expected!");
    }

    @Test
    void testCreateCommandLogOut() throws IncorrectArgumentsCountException, InvalidCommandException {
        String commandMessage = "$ logout";

        Command command = commandFactory.createCommand(commandMessage, selectionKey);
        assertInstanceOf(LogOutCommand.class, command,
            "A LogOutCommand was expected!");
    }

    @Test
    void testCreateCommandRegister() throws IncorrectArgumentsCountException, InvalidCommandException {
        String commandMessage = "$ register username password";

        Command command = commandFactory.createCommand(commandMessage, selectionKey);
        assertInstanceOf(RegisterCommand.class, command, "A RegisterCommand was expected!");
    }

    @Test
    void testCreateCommandSell() throws IncorrectArgumentsCountException, InvalidCommandException {
        String commandMessage = "$ sell --offering=BTC";

        Command command = commandFactory.createCommand(commandMessage, selectionKey);
        assertInstanceOf(SellCommand.class, command, "A SellCommand was expected!");
    }

    @Test
    void testCreateCommandSellInvalidOfferingCode() {
        String commandMessage = "$ sell --wrongOffering=BTC";

        assertThrows(InvalidCommandException.class, () -> commandFactory.createCommand(commandMessage, selectionKey),
            "An InvalidCommandException is expected when the offering code is wrong!");
    }

    @Test
    void testCreateCommandWithdrawMoney() throws IncorrectArgumentsCountException, InvalidCommandException {
        String commandMessage = "$ withdraw-money 10";

        Command command = commandFactory.createCommand(commandMessage, selectionKey);
        assertInstanceOf(WithdrawMoneyCommand.class, command, "A WithdrawMoneyCommand was expected!");
    }

    @Test
    void testCreateCommandWithdrawMoneyInappropriateFormat() {
        String commandMessage = "$ withdraw-money abc";

        assertThrows(InvalidCommandException.class, () -> commandFactory.createCommand(commandMessage, selectionKey),
            "An InvalidCommandException is expected when the money is in an inappropriate format!");
    }

    @Test
    void testCreateCommandWithdrawMoneyWhenZero() {
        String commandMessage = "$ withdraw-money 0";

        assertThrows(InvalidCommandException.class, () -> commandFactory.createCommand(commandMessage, selectionKey),
            "An InvalidCommandException is expected when the money is zero!");
    }

    @Test
    void testCreateCommandWithdrawMoneyWhenNegative() {
        String commandMessage = "$ withdraw-money -10";

        assertThrows(InvalidCommandException.class, () -> commandFactory.createCommand(commandMessage, selectionKey),
            "An InvalidCommandException is expected when the money is negative!");
    }

    @Test
    void testCreateCommandInvalidCommandSymbol() {
        String commandMessage = "@ help";

        assertThrows(InvalidCommandException.class, () -> commandFactory.createCommand(commandMessage, selectionKey),
            "An InvalidCommandException is expected when the commandMessage begin with an invalid command symbol!");
    }

    @Test
    void testCreateCommandWhenIncorrectArgumentsCount() {
        String commandMessage = "$ login username";

        assertThrows(IncorrectArgumentsCountException.class, () -> commandFactory.createCommand(commandMessage, selectionKey),
            "An InvalidCommandException is expected when the arguments' count is incorrect!");
    }

    @Test
    void testCreateCommandNonexistentCommand() {
        String commandMessage = "$ invalidCommand";

        assertThrows(InvalidCommandException.class, () -> commandFactory.createCommand(commandMessage, selectionKey),
            "An InvalidCommandException is expected when the commandMessage contains a non-existent command!");

    }
}
