package colourise.networking;

import java.nio.channels.SocketChannel;

public class Connection {
    private SocketChannel sc;

    public Connection(SocketChannel sc) {
        this.sc = sc;
    }
}
