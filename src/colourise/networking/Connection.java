package colourise.networking;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Arrays;

public abstract class Connection {
    private final SocketChannel sc;

    public Connection(SocketChannel sc) {
        this.sc = sc;
    }

    protected byte[] read(int maximum) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(maximum);
        int received = sc.read(buffer);
        if(received < maximum) {
            return Arrays.copyOf(buffer.array(), received);
        }else{
            return buffer.array();
        }
    }

    protected void write(byte[] bytes) throws IOException {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        sc.write(buffer);
    }

    public abstract boolean buffer();
}
