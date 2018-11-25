package colourise.state.match;

import colourise.ColouriseException;

public abstract class MatchException extends ColouriseException {
    private final Match match;

    public Match getMatch() {
        return match;
    }

    public MatchException(Match m) {
        super();
        match = m;
    }

    public MatchException(String message, Match m) {
        super(message);
        match = m;
    }

    public MatchException(Throwable cause, Match m) {
        super(cause);
        match = m;
    }

    public MatchException(String message, Throwable cause, Match m) {
        super(message, cause);
        match = m;
    }

    public MatchException(String message, Throwable cause, boolean enableSuppression, boolean writableStacktrace, Match m) {
        super(message, cause, enableSuppression, writableStacktrace);
        match = m;
    }
}
