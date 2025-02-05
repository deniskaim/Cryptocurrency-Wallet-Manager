package command;

import command.hierarchy.BuyCommand;
import command.hierarchy.Command;
import command.hierarchy.DepositMoneyCommand;
import command.hierarchy.DisconnectCommand;
import command.hierarchy.GetWalletOverallSummaryCommand;
import command.hierarchy.GetWalletSummaryCommand;
import command.hierarchy.HelpCommand;
import command.hierarchy.ListOfferingsCommand;
import command.hierarchy.LogInCommand;
import command.hierarchy.LogOutCommand;
import command.hierarchy.RegisterCommand;
import command.hierarchy.SellCommand;
import exceptions.command.IncorrectArgumentsCountException;
import exceptions.command.InvalidCommandException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import service.account.UserAccountService;
import service.cryptowallet.CryptoWalletService;

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
    void testCreateCommandDepositMoney() throws IncorrectArgumentsCountException, InvalidCommandException {
        String commandMessage = "$ deposit-money 10";

        Command command = commandFactory.createCommand(commandMessage, selectionKey);
        assertInstanceOf(DepositMoneyCommand.class, command, "A DepositMoneyCommand was expected!");
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
    void testCreateCommandInvalidCommandSymbol() {
        String commandMessage = "@ help";

        assertThrows(InvalidCommandException.class, () -> commandFactory.createCommand(commandMessage, selectionKey),
            "An InvalidCommandException is expected when the commandMessage begin with an invalid command symbol!");
    }

    @Test
    void testCreateCommandNonexistentCommand() {
        String commandMessage = "$ invalidCommand";

        assertThrows(InvalidCommandException.class, () -> commandFactory.createCommand(commandMessage, selectionKey),
            "An InvalidCommandException is expected when the commandMessage contains a non-existent command!");

    }
}
