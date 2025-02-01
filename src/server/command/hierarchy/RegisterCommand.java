package server.command.hierarchy;

import exceptions.UsernameAlreadyTakenException;
import server.system.UserSystem;

public class RegisterCommand implements Command {

    private final String username;
    private final String password;
    private final UserSystem userSystem;

    private static final String SUCCESSFUL_MESSAGE = "You have successfully registered in the system";

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
    public String execute() {
        try {
            userSystem.registerUser(username, password);
            return SUCCESSFUL_MESSAGE;
        } catch (UsernameAlreadyTakenException e) {
            throw new RuntimeException("Register command is unsuccessful! Please, try with another username! This one is already taken!");
        }
    }
}
