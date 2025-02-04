package exceptions;

public class NoActiveInvestmentsException extends Exception {
    public NoActiveInvestmentsException(String message) {
        super(message);
    }

    public NoActiveInvestmentsException(String message, Throwable cause) {
        super(message, cause);
    }
}
