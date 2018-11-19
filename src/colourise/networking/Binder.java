package colourise.networking;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;

public final class Binder {
    public static Connection connect(InetSocketAddress address) throws IOException {

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
