package colourise.server.player;

import colourise.ColouriseException;

public class PlayerException extends ColouriseException {
    private final Player player;

    public Player getPlayer() {
        return player;
    }

    public PlayerException(Player p) {
        super();
        player = p;
    }

    public PlayerException(String message, Player p) {
        super(message);
        player = p;
    }

    public PlayerException(Throwable cause, Player p) {
        super(cause);
        player = p;
    }

    public PlayerException(String message, Throwable cause, Player p) {
        super(message, cause);
        player = p;
    }

    public PlayerException(String message, Throwable cause, boolean enableSuppression, boolean writableStacktrace, Player p) {
        super(message, cause, enableSuppression, writableStacktrace);
        player = p;
    }
}
