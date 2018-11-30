package colourise.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Scanner;

public class Program {
    public static void main(String[] args) {
        try {
            // Creates a game
            Scanner scanner = new Scanner(System.in);
            System.out.print("Please enter a port number for the server to use: ");
            Service game = new Service(new InetSocketAddress(scanner.nextInt()));
            game.listen();
        } catch(IOException ex) {
            ex.printStackTrace();
        }
    }
}
