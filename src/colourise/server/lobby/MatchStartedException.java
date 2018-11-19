package colourise.server.lobby;

import colourise.server.match.Match;

public class MatchStartedException extends LobbyException {
    private final Match match;

    public Match getMatch() {
        return match;
    }

    public MatchStartedException(Lobby l, Match m) {
        super(l);
        match = m;
    }

    public MatchStartedException(String message, Lobby l, Match m) {
        super(message, l);
        match = m;
    }

    public MatchStartedException(Throwable cause, Lobby l, Match m) {
        super(cause, l);
        match = m;
    }

    public MatchStartedException(String message, Throwable cause, Lobby l, Match m) {
        super(message, cause, l);
        match = m;
    }

    public MatchStartedException(String message, Throwable cause, boolean enableSuppression, boolean writableStacktrace, Lobby l, Match m) {
        super(message, cause, enableSuppression, writableStacktrace, l);
        match = m;
    }
}
