package colourise.networking;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;

/**
 * Selecing socket server
 */
public final class Server {
    // Socket selector
    private final Selector selector;
    // Socket channel
    private final ServerSocketChannel ssc;
    // Listener to notify
    private final Listener listener;
    // Whether to keep running
    private boolean run = true;

    Selector getSelector() {
        return selector;
    }

    /**
     * Server constructor.
     * @param selector Selector to perform select operation on.
     * @param ssc Socket channel on bound port.
     * @param listener Listener to notify.
     */
    public Server(Selector selector, ServerSocketChannel ssc, Listener listener) {
        this.selector = selector;
        this.ssc = ssc;
        this.listener = listener;
    }

    /**
     * Listen for activity.
     * @throws IOException
     */
    public void listen() throws IOException {
        run = true;
        while(run) {
            int n = selector.select();
            if(n != 0) { // Activity found
                Set keys = selector.selectedKeys();
                Iterator it = keys.iterator();
                // Process available requests
                while(it.hasNext()) {
                    SelectionKey key = (SelectionKey) it.next();
                    if((key.readyOps() & SelectionKey.OP_ACCEPT) == SelectionKey.OP_ACCEPT) {
                        accept();
                    } else if((key.readyOps() & SelectionKey.OP_READ) == SelectionKey.OP_READ) {
                        read((Connection) key.attachment());
                    }
                    it.remove();
                }
            }
        }
    }

    /**
     * Pauses the server
     */
    public void pause() {
        run = false;
    }

    /**
     * Accepts a new connection
     */
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

    /**
     * Notifies the listener of a readable connection.
     * @param c The connection.
     */
    private void read(Connection c) {
        listener.read(c);
    }

    /**
     * Notifies the listener of a disconnected connection.
     * @param c The connection.
     */
    void disconnected(Connection c) {
        listener.disconnected(c);
    }
}
