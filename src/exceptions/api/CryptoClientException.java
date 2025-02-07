package exceptions.api;

public class CryptoClientException extends Exception {
    public CryptoClientException(String message) {
        super(message);
    }

    public CryptoClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
