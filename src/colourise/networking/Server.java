package colourise.networking;

import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;

public final class Server {
    private Selector selector;
    private ServerSocketChannel ssc;
    private Listener listener;
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
                        accept();
                    } else if((key.readyOps() & SelectionKey.OP_READ) == SelectionKey.OP_READ) {
                        read((SocketChannel) key.channel(), (Connection) key.attachment());
                    }
                }
                keys.clear();
            }
        }
    }

    private void accept() {
        try {
            SocketChannel sc = ssc.accept();
            sc.configureBlocking(false);
            Connection c = new Connection(sc);
            sc.register(selector, SelectionKey.OP_READ, c);
            listener.connected(c);
        }catch(IOException ex){

        }
    }

    private void read(SocketChannel sc, Connection c) {
        listener.read(c);
    }


}
