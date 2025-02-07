package exceptions.wallet;

public class MissingInWalletAssetException extends WalletException {
    public MissingInWalletAssetException(String message) {
        super(message);
    }

    public MissingInWalletAssetException(String message, Throwable cause) {
        super(message, cause);
    }
}
