package colourise;

public class ColouriseException extends Exception {
    public ColouriseException() {
        super();
    }

    public ColouriseException(String message) {
        super(message);
    }

    public ColouriseException(Throwable cause) {
        super(cause);
    }

    public ColouriseException(String message, Throwable cause) {
        super(message, cause);
    }

    public ColouriseException(String message, Throwable cause, boolean enableSuppression, boolean writableStacktrace) {
        super(message, cause, enableSuppression, writableStacktrace);
    }
}
