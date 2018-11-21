package colourise.server;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Program {
    public static void main(String[] args) {
        try {
            Service game = new Service(new InetSocketAddress(9000));
            game.listen();
        } catch(IOException ex) {
            ex.printStackTrace();
        }
    }
}
