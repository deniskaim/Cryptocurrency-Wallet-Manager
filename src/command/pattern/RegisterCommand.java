package command.pattern;

import exceptions.command.UnsuccessfulCommandException;
import exceptions.user.UsernameAlreadyTakenException;
import user.UserAccountService;

public class RegisterCommand implements Command {

    private final String username;
    private final String password;
    private final UserAccountService userAccountService;

    private static final String SUCCESSFUL_MESSAGE = "You have successfully registered in the system";

    public RegisterCommand(String username, String password, UserAccountService userAccountService) {
        if (username == null) {
            throw new IllegalArgumentException("username cannot be null reference!");
        }
        if (password == null) {
            throw new IllegalArgumentException("password cannot be null reference!");
        }
        if (userAccountService == null) {
            throw new IllegalArgumentException("userAccountService cannot be null!");
        }

        this.username = username;
        this.password = password;
        this.userAccountService = userAccountService;
    }

    @Override
    public String execute() throws UnsuccessfulCommandException {
        try {
            userAccountService.registerUser(username, password);
            return SUCCESSFUL_MESSAGE;
        } catch (UsernameAlreadyTakenException e) {
            throw new UnsuccessfulCommandException(
                "Register command is unsuccessful! " + e.getMessage(), e);
        }
    }
}
