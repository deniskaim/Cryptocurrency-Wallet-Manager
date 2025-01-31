package server.command.hierarchy;

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

    public LogInCommand(String[] args, UserSystem userSystem, SelectionKey selectionKey) {
        if (args == null || args.length != 2) {
            throw new IllegalArgumentException("Register command should include just username and password!");
        }
        if (userSystem == null) {
            throw new IllegalArgumentException("userSystem cannot be null!");
        }
        if (selectionKey == null) {
            throw new IllegalArgumentException("selectionKey cannot be null!");
        }
        // todo: add check whether this client is logged in
        this.username = args[0];
        this.password = args[1];
        this.userSystem = userSystem;
        this.selectionKey = selectionKey;
    }

    @Override
    public void execute() {
        try {
            User loggedInUser = userSystem.logInUser(username, password);
            selectionKey.attach(loggedInUser);
        } catch (UserNotFoundException e) {
            throw new RuntimeException("There is no registered user with this username!", e);
        } catch (WrongPasswordException e) {
            throw new RuntimeException("The password is incorrect!", e);
        }
    }
}
