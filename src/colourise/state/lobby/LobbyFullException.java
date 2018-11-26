package colourise.state.lobby;

public class LobbyFullException extends LobbyException {
    public LobbyFullException(Lobby l) {
        super(l);
    }

    public LobbyFullException(String message, Lobby l) {
        super(message, l);
    }

    public LobbyFullException(Throwable cause, Lobby l) {
        super(cause, l);
    }

    public LobbyFullException(String message, Throwable cause, Lobby l) {
        super(message, cause, l);
    }

    public LobbyFullException(String message, Throwable cause, boolean enableSuppression, boolean writableStacktrace, Lobby l) {
        super(message, cause, enableSuppression, writableStacktrace, l);
    }
}
