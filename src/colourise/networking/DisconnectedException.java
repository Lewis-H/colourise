package colourise.networking;

public class DisconnectedException extends NetworkingException {
    public DisconnectedException(Connection c) {
        super(c);
    }

    public DisconnectedException(String message, Connection c) {
        super(message, c);
    }

    public DisconnectedException(Throwable cause, Connection c) {
        super(cause, c);
    }

    public DisconnectedException(String message, Throwable cause, Connection c) {
        super(message, cause, c);
    }

    public DisconnectedException(String message, Throwable cause, boolean enableSuppression, boolean writableStacktrace, Connection c) {
        super(message, cause, enableSuppression, writableStacktrace, c);
    }
}
