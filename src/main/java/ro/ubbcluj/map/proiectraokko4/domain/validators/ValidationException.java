package ro.ubbcluj.map.proiectraokko4.domain.validators;

/**
 * valid eception
 */
public class ValidationException extends RuntimeException {
    /**
     * ve
     */
    public ValidationException() {
    }

    /**
     *
     * @param message msg
     */
    public ValidationException(String message) {
        super(message);
    }

    /**
     *
     * @param message msg
     * @param cause cause
     */
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     *
     * @param cause cause
     */
    public ValidationException(Throwable cause) {
        super(cause);
    }

    /**
     *
     * @param message msg
     * @param cause cause
     * @param enableSuppression e
     * @param writableStackTrace w
     */
    public ValidationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
