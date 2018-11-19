package colourise.networking;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Arrays;

public class Connection {
    private final SocketChannel sc;

    public boolean isConnected() {
        return sc.isConnected();
    }

    Connection(SocketChannel sc) {
        this.sc = sc;
    }

    public byte[] read(int maximum) throws DisconnectedException {
        ByteBuffer buffer = ByteBuffer.allocate(maximum);
        try {
            int received = sc.read(buffer);
            if(received == 0 && maximum != 0) {
                disconnect();
            } else if (received < maximum) {
                return Arrays.copyOf(buffer.array(), received);
            } else {
                return buffer.array();
            }
        } catch(IOException e) {
            disconnect();
        }
        return new byte[0];
    }

    public void disconnect() throws DisconnectedException {
        if(sc.isConnected()) {
            try {
                sc.shutdownInput();
            } catch (IOException e) {
            } finally {
                try {
                    sc.shutdownOutput();
                } catch (IOException e) {
                } finally {
                    try {
                        // Closes socket and cancels selector key
                        sc.close();
                    } catch (IOException e) {
                    } finally {
                    }
                }
            }
        }
        throw new DisconnectedException(this);
    }

    public int write(byte[] bytes) throws DisconnectedException {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        try {
            int wrote = sc.write(buffer);
            if(wrote == 0 && bytes.length != 0)
                disconnect();
            return wrote;
        } catch (IOException e) {
            disconnect();
            return 0;
        }
    }
}
