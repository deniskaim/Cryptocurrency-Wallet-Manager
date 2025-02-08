package exceptions.api;

public class CryptoClientException extends RuntimeException {
    public CryptoClientException(String message) {
        super(message);
    }

    public CryptoClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
