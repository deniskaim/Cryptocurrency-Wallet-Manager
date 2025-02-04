package exceptions.command;

public class IncorrectArgumentsCountException extends CommandException {
    public IncorrectArgumentsCountException(String message) {
        super(message);
    }

    public IncorrectArgumentsCountException(String message, Throwable cause) {
        super(message, cause);
    }
}
