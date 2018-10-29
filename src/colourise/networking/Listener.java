package colourise.networking;

public interface Listener {
    void connected(Connection c);

    void disconnected(Connection c);

    void read(Connection c);
}
