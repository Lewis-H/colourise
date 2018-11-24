package colourise.client;

public class LeftMatchException extends MatchException {
    public LeftMatchException(Match m) {
        super(m);
    }

    public LeftMatchException(String message, Match m) {
        super(message, m);
    }

    public LeftMatchException(Throwable cause, Match m) {
        super(cause, m);
    }

    public LeftMatchException(String message, Throwable cause, Match m) {
        super(message, cause, m);
    }

    public LeftMatchException(String message, Throwable cause, boolean enableSuppression, boolean writableStacktrace, Match m) {
        super(message, cause, enableSuppression, writableStacktrace, m);
    }
}
