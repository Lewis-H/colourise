package colourise.networking;

import java.net.Socket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;

public final class Server {
    private Selector selector;
    private ServerSocketChannel ssc;
    private Listener listener;
    private Map<SocketChannel, Connection> connections = new HashMap<>();
    private boolean run = true;

    public Server(Selector selector, ServerSocketChannel ssc, Listener listener) {
        this.selector = selector;
        this.ssc = ssc;
        this.listener = listener;
    }

    private void listen() {
        while(run) {
            int n = selector.select();
            if(n != 0) {
                Set keys = selector.selectedKeys();
                Iterator it = keys.iterator();
                while(it.hasNext()) {
                    SelectionKey key = (SelectionKey) it.next();
                    if((key.readyOps() & SelectionKey.OP_ACCEPT) == SelectionKey.OP_ACCEPT) {
                        accept(ssc.accept().getChannel());
                    } else if((key.readyOps() & SelectionKey.OP_READ) == SelectionKey.OP_READ) {
                        read((SocketChannel)key.channel());
                    }
                }
                keys.clear();
            }
        }
    }

    private void accept(SocketChannel sc) {
        sc.configureBlocking(false);
        sc.register(selector, SelectionKey.OP_READ);
        Connection c = new Connection(sc);
        connections.put(sc, c);
        listener.connected(c);
    }

    private void read(SocketChannel sc) {

    }


}
