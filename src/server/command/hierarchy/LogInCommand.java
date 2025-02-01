package server.command.hierarchy;

import exceptions.AlreadyLoggedInException;
import exceptions.UserNotFoundException;
import exceptions.WrongPasswordException;
import server.system.User;
import server.system.UserSystem;

import java.nio.channels.SelectionKey;

public class LogInCommand implements Command {

    private final String username;
    private final String password;
    private final UserSystem userSystem;
    private final SelectionKey selectionKey;

    private static final String SUCCESSFUL_MESSAGE = "You have successfully logged in as \"%s\"";

    public LogInCommand(String[] args, UserSystem userSystem, SelectionKey selectionKey) {
        if (args == null || args.length != 2) {
            throw new IllegalArgumentException("LogIn command should include just username and password!");
        }
        if (userSystem == null) {
            throw new IllegalArgumentException("userSystem cannot be null!");
        }
        if (selectionKey == null) {
            throw new IllegalArgumentException("selectionKey cannot be null!");
        }

        this.username = args[0];
        this.password = args[1];
        this.userSystem = userSystem;
        this.selectionKey = selectionKey;
    }

    @Override
    public String execute() throws AlreadyLoggedInException {
        if (selectionKey.attachment() != null) {
            throw new AlreadyLoggedInException(
                "Log in is only possible if the user hasn't logged in any account beforehand!");
        }

        try {
            User loggedInUser = userSystem.logInUser(username, password);
            selectionKey.attach(loggedInUser);
            return String.format(SUCCESSFUL_MESSAGE, username);
        } catch (UserNotFoundException e) {
            throw new RuntimeException(
                "Log in command is unsuccessful! There is no registered user with this username!",
                e);
        } catch (WrongPasswordException e) {
            throw new RuntimeException("Log in command is unsuccessful! The password is incorrect!", e);
        }
    }
}
