package command.hierarchy;

import exceptions.AlreadyLoggedInException;
import exceptions.UserNotFoundException;
import exceptions.WrongPasswordException;
import service.account.UserAccountService;
import user.User;

import java.nio.channels.SelectionKey;

public class LogInCommand implements Command {

    private final String username;
    private final String password;
    private final UserAccountService userAccountService;
    private final SelectionKey selectionKey;

    private static final String SUCCESSFUL_MESSAGE = "You have successfully logged in as \"%s\"";

    public LogInCommand(String[] args, UserAccountService userAccountService, SelectionKey selectionKey) {
        if (args == null || args.length != 2) {
            throw new IllegalArgumentException("LogIn command should include just username and password!");
        }
        if (userAccountService == null) {
            throw new IllegalArgumentException("userAccountService cannot be null!");
        }
        if (selectionKey == null) {
            throw new IllegalArgumentException("selectionKey cannot be null!");
        }

        this.username = args[0];
        this.password = args[1];
        this.userAccountService = userAccountService;
        this.selectionKey = selectionKey;
    }

    @Override
    public String execute() throws AlreadyLoggedInException, UserNotFoundException, WrongPasswordException {
        if (selectionKey.attachment() != null) {
            throw new AlreadyLoggedInException(
                "Log in is only possible if the user hasn't logged in any account beforehand!");
        }

        User loggedInUser = userAccountService.logInUser(username, password);
        selectionKey.attach(loggedInUser);
        return String.format(SUCCESSFUL_MESSAGE, username);
    }
}
