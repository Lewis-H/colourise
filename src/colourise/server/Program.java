package colourise.server;

import colourise.networking.Binder;
import colourise.networking.Server;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Program {
    public static void main(String[] args) {
        try {
            Colourise game = new Colourise(new InetSocketAddress(9000));
            game.listen();
        } catch(IOException ex) {
            ex.printStackTrace();
        }
    }
}
