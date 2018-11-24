package colourise.client;

public class MatchBegunException extends MatchException {
    public MatchBegunException(Match m) {
        super(m);
    }

    public MatchBegunException(String message, Match m) {
        super(message, m);
    }

    public MatchBegunException(Throwable cause, Match m) {
        super(cause, m);
    }

    public MatchBegunException(String message, Throwable cause, Match m) {
        super(message, cause, m);
    }

    public MatchBegunException(String message, Throwable cause, boolean enableSuppression, boolean writableStacktrace, Match m) {
        super(message, cause, enableSuppression, writableStacktrace, m);
    }
}
