package colourise.server.lobby;

import colourise.ColouriseException;
import colourise.server.match.Match;

public class LobbyException extends ColouriseException {
    private final Lobby lobby;

    public Lobby getLobby() {
        return lobby;
    }

    public LobbyException(Lobby l) {
        super();
        lobby = l;
    }

    public LobbyException(String message, Lobby l) {
        super(message);
        lobby = l;
    }

    public LobbyException(Throwable cause, Lobby l) {
        super(cause);
        lobby = l;
    }

    public LobbyException(String message, Throwable cause, Lobby l) {
        super(message, cause);
        lobby = l;
    }

    public LobbyException(String message, Throwable cause, boolean enableSuppression, boolean writableStacktrace, Lobby l) {
        super(message, cause, enableSuppression, writableStacktrace);
        lobby = l;
    }
}
