package server.command.hierarchy;

public class RegisterCommand implements Command {

    private final String username;
    private final String password;

    public RegisterCommand(String[] args) {
        if (args == null || args.length != 2) {
            throw new IllegalArgumentException("Register command should include just username and password!");
        }

        this.username = args[0];
        this.password = args[1];
    }

    @Override
    public void execute() {

    }
}
