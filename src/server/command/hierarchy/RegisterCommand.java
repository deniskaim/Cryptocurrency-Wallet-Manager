package server.command.hierarchy;

public class RegisterCommand implements Command {

    private final String username;
    private final String password;

    public RegisterCommand(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public void execute() {

    }
}
