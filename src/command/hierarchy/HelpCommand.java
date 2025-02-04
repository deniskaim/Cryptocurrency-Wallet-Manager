package command.hierarchy;

public class HelpCommand implements Command {

    private static final String REGISTER_EXAMPLE = "$ register <username> <password>" + System.lineSeparator();
    private static final String LOGIN_EXAMPLE = "$ login <username> <password>" + System.lineSeparator();
    private static final String LOGOUT_EXAMPLE = "$ logout" + System.lineSeparator();
    private static final String DEPOSIT_MONEY_EXAMPLE = "$ deposit-money <amount>" + System.lineSeparator();
    private static final String LIST_OFFERINGS_EXAMPLE = "$ list-offerings" + System.lineSeparator();
    private static final String BUY_EXAMPLE = "$ buy --offering=<assetID> --money=<amount>" + System.lineSeparator();
    private static final String SELL_EXAMPLE = "$ sell --offering=<assetID>" + System.lineSeparator();
    private static final String GET_WALLET_SUMMARY_EXAMPLE = "$ get-wallet-summary" + System.lineSeparator();
    private static final String GET_WALLET_OVERALL_SUMMARY_EXAMPLE =
        "$ get-wallet-overall-summary" + System.lineSeparator();
    private static final String DISCONNECT_MESSAGE = "$ disconnect" + System.lineSeparator();

    private static final String HELP_MESSAGE =
        "You can choose from the following commands:" + System.lineSeparator() +
            REGISTER_EXAMPLE + LOGIN_EXAMPLE + LOGOUT_EXAMPLE + DEPOSIT_MONEY_EXAMPLE + LIST_OFFERINGS_EXAMPLE +
            BUY_EXAMPLE + SELL_EXAMPLE + GET_WALLET_SUMMARY_EXAMPLE + GET_WALLET_OVERALL_SUMMARY_EXAMPLE +
            DISCONNECT_MESSAGE +
            "Keep in mind that the commands should start with $ " +
            "and you have to be logged in to use the functionalities of the app.";

    @Override
    public String execute() {
        return HELP_MESSAGE;
    }
}
