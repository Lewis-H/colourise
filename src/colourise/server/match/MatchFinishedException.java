package colourise.server.match;

public final class MatchFinishedException extends MatchException {
    public MatchFinishedException(Match m) {
        super(m);
    }

    public MatchFinishedException(String message, Match m) {
        super(message, m);
    }

    public MatchFinishedException(Throwable cause, Match m) {
        super(cause, m);
    }

    public MatchFinishedException(String message, Throwable cause, Match m) {
        super(message, cause, m);
    }

    public MatchFinishedException(String message, Throwable cause, boolean enableSuppression, boolean writableStacktrace, Match m) {
        super(message, cause, enableSuppression, writableStacktrace, m);
    }
}
