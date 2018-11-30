package colourise.bot;

import colourise.networking.Binder;
import colourise.networking.Connection;
import colourise.networking.DisconnectedException;
import colourise.state.match.CannotPlayException;
import colourise.state.match.InvalidPositionException;
import colourise.state.match.MatchFinishedException;
import colourise.state.match.NotPlayersTurnException;
import colourise.state.match.CardAlreadyUsedException;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Scanner;

public class Program {
    public static void main(String[] args) {
        // Ask for server details
        System.out.println("Meep morp, I am a bot.");
        Scanner scanner = new Scanner(System.in);
        System.out.print("Please enter the host: ");
        String host = scanner.nextLine();
        System.out.print("Please enter the port: ");
        int port = scanner.nextInt();
        System.out.println("Thanks. Connecting...");
        try {
            Connection connection = Binder.connect(new InetSocketAddress(host, port));
            try {
                Bot bot = new Bot(connection);
                System.out.println("We're connected!");
                bot.start();
            } catch(DisconnectedException ex) {
                System.out.println("The server said goodbye. Goodbye!");
            } catch(MatchFinishedException ex) {
                System.out.println("Game over!");
            } catch(NotPlayersTurnException | CannotPlayException | InvalidPositionException | CardAlreadyUsedException ex) {
                System.err.println("Uh oh...");
                ex.printStackTrace();
            } finally {
                connection.disconnect();
            }
        } catch(IOException ex) {
            System.out.println("Hmm... I can't seem to reach the server?");
            ex.printStackTrace();
        }
    }
}
