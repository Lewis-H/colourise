package colourise.state.match;

public final class CannotPlayException extends MatchException {
    private final int row, column;
    private final Player player;

    public Player getPlayer() {
        return player;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public CannotPlayException(Match m, Player p, int r, int c) {
        super(m);
        player = p;
        row = r;
        column = c;
    }

    public CannotPlayException(String message, Match m, Player p, int r, int c) {
        super(message, m);
        player = p;
        row = r;
        column = c;
    }

    public CannotPlayException(Throwable cause, Match m, Player p, int r, int c) {
        super(cause, m);
        player = p;
        row = r;
        column = c;
    }

    public CannotPlayException(String message, Throwable cause, Match m, Player p, int r, int c) {
        super(message, cause, m);
        player = p;
        row = r;
        column = c;
    }

    public CannotPlayException(String message, Throwable cause, boolean enableSuppression, boolean writableStacktrace, Match m, Player p, int r, int c) {
        super(message, cause, enableSuppression, writableStacktrace, m);
        player = p;
        row = r;
        column = c;
    }
}