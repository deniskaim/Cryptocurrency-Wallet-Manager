package command.hierarchy;

import exceptions.UsernameAlreadyTakenException;
import service.account.UserAccountService;

public class RegisterCommand implements Command {

    private final String username;
    private final String password;
    private final UserAccountService userAccountService;

    private static final String SUCCESSFUL_MESSAGE = "You have successfully registered in the system";

    public RegisterCommand(String[] args, UserAccountService userAccountService) {
        if (args == null || args.length != 2) {
            throw new IllegalArgumentException("Register command should include just username and password!");
        }
        if (userAccountService == null) {
            throw new IllegalArgumentException("userAccountService cannot be null!");
        }

        this.username = args[0];
        this.password = args[1];
        this.userAccountService = userAccountService;
    }

    @Override
    public String execute() throws UsernameAlreadyTakenException {
        userAccountService.registerUser(username, password);
        return SUCCESSFUL_MESSAGE;
    }
}
