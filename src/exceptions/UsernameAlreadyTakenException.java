package exceptions;

public class UsernameAlreadyTakenException extends Exception {
    public UsernameAlreadyTakenException(String message) {
        super(message);
    }

    public UsernameAlreadyTakenException(String message, Throwable cause) {
        super(message, cause);
    }
}
