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
import cryptowallet.CryptoWalletService;
import user.UserAccountService;

import java.nio.channels.SelectionKey;
import java.util.Arrays;

import static utils.TextUtils.getSubstringsFromString;
import static utils.TextUtils.getTheRestOfTheString;

public class CommandFactory {

    private static final String COMMAND_SYMBOL = "$";
    private static final String REGISTER_MESSAGE = "register";
    private static final String LOG_IN_MESSAGE = "login";
    private static final String LOG_OUT_MESSAGE = "logout";
    private static final String DISCONNECT_MESSAGE = "disconnect";
    private static final String DEPOSIT_MONEY_MESSAGE = "deposit-money";
    private static final String WITHDRAW_MONEY_MESSAGE = "withdraw-money";
    private static final String LIST_OFFERINGS_MESSAGE = "list-offerings";
    private static final String BUY_MESSAGE = "buy";
    private static final String SELL_MESSAGE = "sell";
    private static final String GET_WALLET_SUMMARY_MESSAGE = "get-wallet-summary";
    private static final String GET_WALLET_OVERALL_SUMMARY_MESSAGE = "get-wallet-overall-summary";
    private static final String HELP_MESSAGE = "help";

    private static final String OFFERING_CODE_INPUT_MESSAGE = "--offering=";
    private static final String MONEY_INPUT_MESSAGE = "--money=";

    private static CommandFactory instance;
    private final UserAccountService userAccountService;
    private final CryptoWalletService cryptoWalletService;

    private CommandFactory(UserAccountService userAccountService, CryptoWalletService cryptoWalletService) {
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

    public Command createCommand(String commandMessage, SelectionKey selectionKey)
        throws InvalidCommandException, IncorrectArgumentsCountException {
        if (commandMessage == null) {
            throw new IllegalArgumentException("commandMessage cannot be null reference");
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
            case REGISTER_MESSAGE -> createRegisterCommand(args);
            case LOG_IN_MESSAGE -> createLogInCommand(args, selectionKey);
            case LOG_OUT_MESSAGE -> createLogOut(args, selectionKey);
            case DISCONNECT_MESSAGE -> createDisconnectCommand(args, selectionKey);
            case DEPOSIT_MONEY_MESSAGE -> createDepositMoneyCommand(args, selectionKey);
            case WITHDRAW_MONEY_MESSAGE -> createWithdrawMoneyCommand(args, selectionKey);
            case LIST_OFFERINGS_MESSAGE -> createListOfferings(args, selectionKey);
            case BUY_MESSAGE -> createBuyCommand(args, selectionKey);
            case SELL_MESSAGE -> createSellCommand(args, selectionKey);
            case GET_WALLET_SUMMARY_MESSAGE -> createGetWalletSummaryCommand(args, selectionKey);
            case GET_WALLET_OVERALL_SUMMARY_MESSAGE -> createGetWalletOverallSummaryCommand(args, selectionKey);
            case HELP_MESSAGE -> createHelpCommand(args);
            default -> throw new InvalidCommandException("That is an invalid command. Try again!");
        };
    }

    private void validateCommandSymbol(String commandSymbol) throws InvalidCommandException {
        if (!commandSymbol.equals(COMMAND_SYMBOL)) {
            throw new InvalidCommandException("Invalid begin symbol for a command message!");
        }
    }

    private void validateArguments(String[] args, int expectedCount) throws IncorrectArgumentsCountException {
        if (args == null) {
            throw new IllegalArgumentException("args cannot be null reference!");
        }
        if (args.length != expectedCount) {
            throw new IncorrectArgumentsCountException("Incorrect amount of parameters!");
        }
    }

    private BuyCommand createBuyCommand(String[] args, SelectionKey selectionKey)
        throws IncorrectArgumentsCountException, InvalidCommandException {
        validateArguments(args, 2);

        String assetIDString = getTheRestOfTheString(args[0], OFFERING_CODE_INPUT_MESSAGE);
        if (assetIDString == null) {
            throw new InvalidCommandException("Offering string is invalid!");
        }

        double amount;
        String amountString = getTheRestOfTheString(args[1], MONEY_INPUT_MESSAGE);
        if (amountString == null) {
            throw new InvalidCommandException("Money string is invalid!");
        }

        try {
            amount = Double.parseDouble(amountString);
        } catch (NumberFormatException e) {
            throw new InvalidCommandException("The amount in the buy-money command is not in an appropriate format",
                e);
        }
        if (Double.compare(amount, 0d) <= 0) {
            throw new InvalidCommandException("The amount in the buy-money command cannot be below 0.00 USD!");
        }

        return new BuyCommand(assetIDString, amount, cryptoWalletService, selectionKey);
    }

    private DepositMoneyCommand createDepositMoneyCommand(String[] args, SelectionKey selectionKey)
        throws IncorrectArgumentsCountException, InvalidCommandException {
        validateArguments(args, 1);

        double amount;
        try {
            amount = Double.parseDouble(args[0]);
        } catch (NumberFormatException e) {
            throw new InvalidCommandException("The amount in the deposit-money command is not in an appropriate format",
                e);
        }

        if (Double.compare(amount, 0d) <= 0) {
            throw new InvalidCommandException("The amount in the deposit-money command cannot be below 0.00 USD!");
        }

        return new DepositMoneyCommand(amount, selectionKey);
    }

    private DisconnectCommand createDisconnectCommand(String[] args, SelectionKey selectionKey)
        throws IncorrectArgumentsCountException {
        validateArguments(args, 0);

        return new DisconnectCommand(selectionKey);
    }

    private GetWalletOverallSummaryCommand createGetWalletOverallSummaryCommand(String[] args,
                                                                                SelectionKey selectionKey)
        throws IncorrectArgumentsCountException {
        validateArguments(args, 0);

        return new GetWalletOverallSummaryCommand(cryptoWalletService, selectionKey);
    }

    private GetWalletSummaryCommand createGetWalletSummaryCommand(String[] args, SelectionKey selectionKey)
        throws IncorrectArgumentsCountException {
        validateArguments(args, 0);

        return new GetWalletSummaryCommand(selectionKey);
    }

    private HelpCommand createHelpCommand(String[] args) throws IncorrectArgumentsCountException {
        validateArguments(args, 0);

        return new HelpCommand();
    }

    private ListOfferingsCommand createListOfferings(String[] args, SelectionKey selectionKey)
        throws IncorrectArgumentsCountException {
        validateArguments(args, 0);

        return new ListOfferingsCommand(cryptoWalletService, selectionKey);
    }

    private LogInCommand createLogInCommand(String[] args, SelectionKey selectionKey)
        throws IncorrectArgumentsCountException {
        validateArguments(args, 2);

        String username = args[0];
        String password = args[1];

        return new LogInCommand(username, password, userAccountService, selectionKey);
    }

    private LogOutCommand createLogOut(String[] args, SelectionKey selectionKey)
        throws IncorrectArgumentsCountException {
        validateArguments(args, 0);

        return new LogOutCommand(selectionKey);
    }

    private RegisterCommand createRegisterCommand(String[] args) throws IncorrectArgumentsCountException {
        validateArguments(args, 2);

        String username = args[0];
        String password = args[1];

        return new RegisterCommand(username, password, userAccountService);
    }

    private SellCommand createSellCommand(String[] args, SelectionKey selectionKey)
        throws IncorrectArgumentsCountException, InvalidCommandException {
        validateArguments(args, 1);

        String assetIDString = getTheRestOfTheString(args[0], OFFERING_CODE_INPUT_MESSAGE);
        if (assetIDString == null) {
            throw new InvalidCommandException("Offering string is invalid!");
        }

        return new SellCommand(assetIDString, cryptoWalletService, selectionKey);
    }

    private WithdrawMoneyCommand createWithdrawMoneyCommand(String[] args, SelectionKey selectionKey)
        throws IncorrectArgumentsCountException, InvalidCommandException {
        validateArguments(args, 1);

        double amount;
        try {
            amount = Double.parseDouble(args[0]);
        } catch (NumberFormatException e) {
            throw new InvalidCommandException(
                "The amount in the withdraw-money command is not in an appropriate format",
                e);
        }

        if (Double.compare(amount, 0d) <= 0) {
            throw new InvalidCommandException("The amount in the withdraw-money command cannot be below 0.00 USD!");
        }

        return new WithdrawMoneyCommand(amount, selectionKey);
    }

}
