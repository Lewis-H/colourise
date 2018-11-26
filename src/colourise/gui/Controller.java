package colourise.gui;

import colourise.client.Game;
import colourise.client.Stage;
import colourise.networking.Binder;
import colourise.networking.Connection;
import colourise.networking.DisconnectedException;
import colourise.networking.protocol.Message;
import colourise.networking.protocol.Parser;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.InetSocketAddress;

public class Controller {
    private Dialogue dialogue = new Dialogue(this::connectClicked);
    private Lobby lobby;
    private Connection connection;
    private Game game;
    private Parser parser;

    private void connectClicked(ActionEvent e) {
        String host = dialogue.getHost();
        int port = dialogue.getPort();
        try {
            connection = Binder.connect(new InetSocketAddress(host, port));
            dialogue.setVisible(false);
            game = new Game();
            parser = new Parser();
            try {
                while (game.getStage() != Stage.LOBBY)
                    game.update(read(connection));
                lobby = new Lobby(game.isLeader());
                for(int i = 0; i < game.size(); i++)
                    lobby.increment();
                lobby.pack();
                lobby.setSize(300, 200);
                lobby.setLocationRelativeTo(null);
                lobby.setVisible(true);
            } catch(DisconnectedException ex) {
                JOptionPane.showMessageDialog(dialogue, "Host unexpectedly closed connection.");
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(dialogue, ex.getMessage());
        }
    }

    public void start() {
        dialogue.setLocationRelativeTo(null);
        dialogue.setVisible(true);
    }

    public Message read(Connection connection) throws DisconnectedException {
        while (parser.getRemaining() != 0)
            parser.add(connection.read(parser.getRemaining()));
        Message message = parser.create();
        parser.reset();
        return message;
    }
}
