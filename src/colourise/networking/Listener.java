package colourise.networking;

/**
 * Listener interface for the server to implement.
 */
public interface Listener {
    /**
     * Notify that a connection has connected to the server.
     * @param c The connection.
     */
    void connected(Connection c);

    /**
     * Notify that a connection has disconnected from the server
     * @param c The connection.
     */
    void disconnected(Connection c);

    /**
     * Notify that there is data available to be read from a connection.
     * @param c The connection.
     */
    void read(Connection c);
}
