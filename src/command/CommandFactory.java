package command;

import command.hierarchy.BuyCommand;
import command.hierarchy.Command;
import command.hierarchy.DepositMoneyCommand;
import command.hierarchy.DisconnectCommand;
import command.hierarchy.GetWalletSummaryCommand;
import command.hierarchy.HelpCommand;
import command.hierarchy.ListOfferingsCommand;
import command.hierarchy.LogInCommand;
import command.hierarchy.LogOutCommand;
import command.hierarchy.RegisterCommand;
import command.hierarchy.SellCommand;
import service.cryptowallet.CryptoWalletService;
import service.account.UserAccountService;

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
    private static final String HELP_MESSAGE = "help";

    private static CommandFactory instance;
    private final UserAccountService userAccountService;
    private final CryptoWalletService cryptoWalletService;

    private CommandFactory(UserAccountService userAccountService, CryptoWalletService cryptoWalletService) {
        if (userAccountService == null) {
            throw new IllegalArgumentException("userAccountService cannot be null reference!");
        }
        if (cryptoWalletService == null) {
            throw new IllegalArgumentException("cryptoWalletService cannot be null reference!");
        }
        this.userAccountService = userAccountService;
        this.cryptoWalletService = cryptoWalletService;
    }

    public static CommandFactory getInstance(UserAccountService userAccountService,
                                             CryptoWalletService cryptoWalletService) {
        if (instance == null) {
            instance = new CommandFactory(userAccountService, cryptoWalletService);
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
            case DEPOSIT_MONEY_MESSAGE -> new DepositMoneyCommand(args, cryptoWalletService, selectionKey);
            case LIST_OFFERINGS_MESSAGE -> new ListOfferingsCommand(args, cryptoWalletService, selectionKey);
            case BUY_MESSAGE -> new BuyCommand(args, cryptoWalletService, selectionKey);
            case SELL_MESSAGE -> new SellCommand(args, cryptoWalletService, selectionKey);
            case GET_WALLET_SUMMARY_MESSAGE -> new GetWalletSummaryCommand(args, selectionKey);
            // case GET_WALLET_OVERALL_SUMMARY_MESSAGE -> new
            case HELP_MESSAGE -> new HelpCommand();
            default -> throw new IllegalArgumentException("That is an invalid command. Try again!");
        };
    }

    private void validateCommandSymbol(String commandSymbol) {
        if (!commandSymbol.equals(COMMAND_SYMBOL)) {
            throw new IllegalArgumentException("Invalid begin symbol for a command message!");
        }
    }
}
