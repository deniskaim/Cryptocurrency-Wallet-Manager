package server.command.hierarchy;

public class RegisterCommand implements Command {

    private final String username;
    private final String password;

    public RegisterCommand(String username, String password) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Invalid Username for registration!");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Invalid Password for registration!");
        }
        this.username = username;
        this.password = password;
    }

    @Override
    public void execute() {

    }
}
