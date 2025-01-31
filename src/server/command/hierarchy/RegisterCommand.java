package server.command.hierarchy;

import exceptions.UsernameAlreadyTakenException;
import server.system.UserSystem;

public class RegisterCommand implements Command {

    private final String username;
    private final String password;
    private final UserSystem userSystem;

    public RegisterCommand(String[] args, UserSystem userSystem) {
        if (args == null || args.length != 2) {
            throw new IllegalArgumentException("Register command should include just username and password!");
        }
        if (userSystem == null) {
            throw new IllegalArgumentException("userSystem cannot be null!");
        }

        this.username = args[0];
        this.password = args[1];
        this.userSystem = userSystem;
    }

    @Override
    public void execute() {
        try {
            userSystem.registerUser(username, password);
        } catch (UsernameAlreadyTakenException e) {
            throw new RuntimeException("Log in command is unsuccessful! Please, try with another one");
        }
    }
}
