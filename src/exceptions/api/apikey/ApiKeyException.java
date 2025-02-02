package exceptions.api.apikey;

import exceptions.api.CryptoClientException;

public class ApiKeyException extends CryptoClientException {
    public ApiKeyException(String message) {
        super(message);
    }

    public ApiKeyException(String message, Throwable cause) {
        super(message, cause);
    }
}
