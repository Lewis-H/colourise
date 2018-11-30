package colourise.networking;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * Connection/listening socket creation utility.
 */
public final class Binder {
    public static Connection connect(InetSocketAddress address) throws IOException {
        SocketChannel channel = SocketChannel.open(address);
        channel.configureBlocking(true);
        return new Connection(channel);
    }

    public static Server listen(InetSocketAddress address, Listener listener) throws IOException {
        Selector selector = Selector.open();
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);
        ssc.register(selector, SelectionKey.OP_ACCEPT);
        ssc.bind(address);
        return new Server(selector, ssc, listener);
    }
}
