package colourise.bot;

import colourise.networking.DisconnectedException;
import colourise.state.match.CannotPlayException;
import colourise.state.match.InvalidPositionException;
import colourise.state.match.MatchFinishedException;
import colourise.state.match.NotPlayersTurnException;
import colourise.state.player.CardAlreadyUsedException;

import java.io.IOException;
import java.util.Scanner;

public class Program {
    public static void main(String[] args) {
        System.out.println("Meep morp, I am a bot.");
        Scanner scanner = new Scanner(System.in);
        System.out.print("Please enter the host: ");
        String host = scanner.nextLine();
        System.out.print("Please enter the port: ");
        int port = scanner.nextInt();
        System.out.println("Thanks. Connecting...");
        try {
            Bot bot = new Bot(host, port);
            System.out.println("Et voila, we're connected.");
            bot.start();
        } catch(DisconnectedException ex) {
            System.out.println("The server said goodbye. Goodbye!");
        } catch(MatchFinishedException ex) {
            System.out.println("Game over!");
        } catch(NotPlayersTurnException | CannotPlayException | InvalidPositionException | CardAlreadyUsedException ex) {
            System.err.println("Uh oh...");
            ex.printStackTrace();
        } catch(IOException ex) {
            System.out.println("Hmm... I can't seem to reach the server?");
            System.out.println("Maybe this will help: " + ex.getMessage());
        }
    }
}
