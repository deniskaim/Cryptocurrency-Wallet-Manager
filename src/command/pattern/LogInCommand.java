package command.pattern;

import exceptions.command.IncorrectArgumentsCountException;
import exceptions.command.UnsuccessfulCommandException;
import exceptions.user.AlreadyLoggedInException;
import exceptions.user.UserNotFoundException;
import exceptions.user.WrongPasswordException;
import user.UserAccountService;
import user.User;

import java.nio.channels.SelectionKey;

public class LogInCommand implements Command {

    private final String username;
    private final String password;
    private final UserAccountService userAccountService;
    private final SelectionKey selectionKey;

    private static final String SUCCESSFUL_MESSAGE = "You have successfully logged in as \"%s\"";

    public LogInCommand(String[] args, UserAccountService userAccountService, SelectionKey selectionKey)
        throws IncorrectArgumentsCountException {
        if (args == null) {
            throw new IllegalArgumentException("args in Login command cannot be null reference!");
        }
        if (args.length != 2) {
            throw new IncorrectArgumentsCountException("Login command should include just username and password!");
        }
        if (userAccountService == null) {
            throw new IllegalArgumentException("userAccountService cannot be null reference!");
        }
        if (selectionKey == null) {
            throw new IllegalArgumentException("selectionKey cannot be null reference!");
        }

        this.username = args[0];
        this.password = args[1];
        this.userAccountService = userAccountService;
        this.selectionKey = selectionKey;
    }

    @Override
    public String execute()
        throws UnsuccessfulCommandException {
        try {
            if (selectionKey.attachment() != null) {
                throw new AlreadyLoggedInException(
                    "Log in is only possible if the user hasn't logged in any account beforehand!");
            }
            User loggedInUser = userAccountService.logInUser(username, password);
            selectionKey.attach(loggedInUser);
            return String.format(SUCCESSFUL_MESSAGE, username);
        } catch (AlreadyLoggedInException | WrongPasswordException | UserNotFoundException e) {
            throw new UnsuccessfulCommandException(
                "Login command is unsuccessful! " + e.getMessage(), e);
        }
    }
}
