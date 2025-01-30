package server.command;

import server.command.hierarchy.Command;
import server.command.hierarchy.RegisterCommand;

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

    public static Command createCommand(String commandMessage) {
        String[] stringsInCommandMessage = getSubstringsFromString(commandMessage);

        String commandSymbol = stringsInCommandMessage[0];
        if (!commandSymbol.equals(COMMAND_SYMBOL)) {
            throw new IllegalArgumentException("Invalid begin symbol for a command message!");
        }

        String actualCommandString = stringsInCommandMessage[1];
        String[] args = Arrays.copyOfRange(stringsInCommandMessage, 2, stringsInCommandMessage.length);

        return switch (actualCommandString) {
            case REGISTER_MESSAGE -> new RegisterCommand(args);
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
