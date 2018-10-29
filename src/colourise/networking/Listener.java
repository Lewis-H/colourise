package colourise.networking;

public interface Listener {
    void connected(Connection c);

    void disconnected(Connection c);

    void received(Connection c, byte[] data);
}
