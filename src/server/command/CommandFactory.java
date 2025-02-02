package server.command;

import server.command.hierarchy.Command;
import server.command.hierarchy.DepositMoneyCommand;
import server.command.hierarchy.DisconnectCommand;
import server.command.hierarchy.LogInCommand;
import server.command.hierarchy.LogOutCommand;
import server.command.hierarchy.RegisterCommand;
import server.system.UserAccountService;
import server.system.UserRepository;

import java.nio.channels.SelectionKey;
import java.util.Arrays;

import static utils.TextUtils.getSubstringsFromString;

public class CommandFactory {

    private static final String COMMAND_SYMBOL = "$";
    private static final String REGISTER_MESSAGE = "register";
    private static final String LOG_IN_MESSAGE = "login";
    private static final String LOG_OUT_MESSAGE = "logout";
    private static final String DISCONNECT_MESSAGE = "disconnect";
    private static final String DEPOSIT_MONEY_MESSAGE = "deposit-money";
    private static final String LIST_OFFERINGS_MESSAGE = "list-offerings";
    private static final String BUY_MESSAGE = "buy";
    private static final String SELL_MESSAGE = "sell";
    private static final String GET_WALLET_SUMMARY_MESSAGE = "get-wallet-summary";
    private static final String GET_WALLET_OVERALL_SUMMARY_MESSAGE = "get-wallet-overall-summary";

    private static CommandFactory instance;
    private final UserAccountService userAccountService;

    private CommandFactory(UserAccountService userAccountService) {
        if (userAccountService == null) {
            throw new IllegalArgumentException("userAccountService cannot be null reference!");
        }
        this.userAccountService = userAccountService;
    }

    public static CommandFactory getInstance(UserAccountService userAccountService) {
        if (instance == null) {
            instance = new CommandFactory(userAccountService);
        }
        return instance;
    }

    public Command createCommand(String commandMessage, SelectionKey selectionKey) {
        if (commandMessage == null) {
            throw new IllegalArgumentException("input cannot be null reference");
        }
        if (selectionKey == null) {
            throw new IllegalArgumentException("selectionKey cannot be null!");
        }
        String[] stringsInCommandMessage = getSubstringsFromString(commandMessage);

        String commandSymbol = stringsInCommandMessage[0];
        validateCommandSymbol(commandSymbol);

        String actualCommandString = stringsInCommandMessage[1];
        String[] args = Arrays.copyOfRange(stringsInCommandMessage, 2, stringsInCommandMessage.length);

        return switch (actualCommandString) {
            case REGISTER_MESSAGE -> new RegisterCommand(args, userAccountService);
            case LOG_IN_MESSAGE -> new LogInCommand(args, userAccountService, selectionKey);
            case LOG_OUT_MESSAGE -> new LogOutCommand(args, selectionKey);
            case DISCONNECT_MESSAGE -> new DisconnectCommand(args, selectionKey);
            case DEPOSIT_MONEY_MESSAGE -> new DepositMoneyCommand(args, selectionKey);
            // case LIST_OFFERINGS_MESSAGE -> new
            // case BUY_MESSAGE -> new
            // case SELL_MESSAGE -> new
            // case GET_WALLET_SUMMARY_MESSAGE -> new
            // case GET_WALLET_OVERALL_SUMMARY_MESSAGE -> new
            default -> throw new IllegalArgumentException("That is an invalid command. Try again!");
        };
    }

    private void validateCommandSymbol(String commandSymbol) {
        if (!commandSymbol.equals(COMMAND_SYMBOL)) {
            throw new IllegalArgumentException("Invalid begin symbol for a command message!");
        }
    }
}
