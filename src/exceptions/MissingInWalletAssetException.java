package exceptions;

public class MissingInWalletAssetException extends Exception {

    public MissingInWalletAssetException(String message) {
        super(message);
    }

    public MissingInWalletAssetException(String message, Throwable cause) {
        super(message, cause);
    }
}
