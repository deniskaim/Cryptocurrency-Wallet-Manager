package exceptions.wallet;

public class NoActiveInvestmentsException extends WalletException {
    public NoActiveInvestmentsException(String message) {
        super(message);
    }

    public NoActiveInvestmentsException(String message, Throwable cause) {
        super(message, cause);
    }
}
