package server.command;

import server.command.hierarchy.Command;
import server.command.hierarchy.RegisterCommand;
import server.system.UserSystem;

import java.nio.channels.SocketChannel;
import java.util.Arrays;

import static utils.TextUtils.getSubstringsFromString;

public class CommandFactory {

    private static final String COMMAND_SYMBOL = "$";
    private static final String REGISTER_MESSAGE = "register";
    private static final String LOG_IN_MESSAGE = "login";
    private static final String LOG_OUT_MESSAGE = "logout";
    private static final String DEPOSIT_MONEY_MESSAGE = "deposit-money";
    private static final String LIST_OFFERINGS_MESSAGE = "list-offerings";
    private static final String BUY_MESSAGE = "buy";
    private static final String SELL_MESSAGE = "sell";
    private static final String GET_WALLET_SUMMARY_MESSAGE = "get-wallet-summary";
    private static final String GET_WALLET_OVERALL_SUMMARY_MESSAGE = "get-wallet-overall-summary";

    private static CommandFactory instance;
    private UserSystem userSystem;

    private CommandFactory(UserSystem userSystem) {
        this.userSystem = userSystem;
    }

    public static CommandFactory getInstance(UserSystem userSystem) {
        if (userSystem == null) {
            throw new IllegalArgumentException("userSystem cannot be null!");
        }
        if (instance == null) {
            instance = new CommandFactory(userSystem);
        }
        return instance;
    }

    public Command createCommand(String commandMessage, SocketChannel socketChannel) {
        if (socketChannel == null) {
            throw new IllegalArgumentException("socketChannel cannot be null!");
        }
        if (commandMessage == null) {
            throw new IllegalArgumentException("input cannot be null reference");
        }
        String[] stringsInCommandMessage = getSubstringsFromString(commandMessage);

        String commandSymbol = stringsInCommandMessage[0];
        if (!commandSymbol.equals(COMMAND_SYMBOL)) {
            throw new IllegalArgumentException("Invalid begin symbol for a command message!");
        }

        String actualCommandString = stringsInCommandMessage[1];
        String[] args = Arrays.copyOfRange(stringsInCommandMessage, 2, stringsInCommandMessage.length);

        return switch (actualCommandString) {
            case REGISTER_MESSAGE -> new RegisterCommand(args, userSystem);
            // case LOG_IN_MESSAGE -> new
            // case LOG_OUT_MESSAGE -> new
            // case DEPOSIT_MONEY_MESSAGE -> new
            // case LIST_OFFERINGS_MESSAGE -> new
            // case BUY_MESSAGE -> new
            // case SELL_MESSAGE -> new
            // case GET_WALLET_SUMMARY_MESSAGE -> new
            // case GET_WALLET_OVERALL_SUMMARY_MESSAGE -> new
            default -> throw new IllegalArgumentException("That is an invalid command. Try again!");
        };
    }
}
