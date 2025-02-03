package exceptions;

public class InvalidAssetException extends Exception {
    public InvalidAssetException(String message) {
        super(message);
    }

    public InvalidAssetException(String message, Throwable cause) {
        super(message, cause);
    }
}
