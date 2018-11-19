package colourise.networking;

import colourise.ColouriseException;

public class NetworkingException extends ColouriseException {
    private final Connection connection;

    public Connection getConnection() {
        return connection;
    }

    public NetworkingException(Connection c) {
        super();
        connection = c;
    }

    public NetworkingException(String message, Connection c) {
        super(message);
        connection = c;
    }

    public NetworkingException(Throwable cause, Connection c) {
        super(cause);
        connection = c;
    }

    public NetworkingException(String message, Throwable cause, Connection c) {
        super(message, cause);
        connection = c;
    }

    public NetworkingException(String message, Throwable cause, boolean enableSuppression, boolean writableStacktrace, Connection c) {
        super(message, cause, enableSuppression, writableStacktrace);
        connection = c;
    }
}
