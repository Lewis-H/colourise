package colourise.state.match;

import colourise.state.player.Player;

public final class NotPlayersTurnException extends MatchException {
    private Player player;

    public Player getPlayer() {
        return player;
    }

    public NotPlayersTurnException(Match m, Player p) {
        super(m);
        player = p;
    }

    public NotPlayersTurnException(String message, Match m, Player p) {
        super(message, m);
        player = p;
    }

    public NotPlayersTurnException(Throwable cause, Match m, Player p) {
        super(cause, m);
        player = p;
    }

    public NotPlayersTurnException(String message, Throwable cause, Match m, Player p) {
        super(message, cause, m);
        player = p;
    }

    public NotPlayersTurnException(String message, Throwable cause, boolean enableSuppression, boolean writableStacktrace, Match m, Player p) {
        super(message, cause, enableSuppression, writableStacktrace, m);
        player = p;
    }
}
