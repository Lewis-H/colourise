package colourise.gui;

import colourise.client.*;
import colourise.networking.Connection;
import colourise.networking.DisconnectedException;
import colourise.networking.protocol.Command;
import colourise.networking.protocol.Message;
import colourise.networking.protocol.Parser;
import colourise.synchronisation.Consumer;
import colourise.synchronisation.Producer;
import colourise.synchronisation.RunnableConsumer;

public class Controller extends RunnableConsumer<Message> {
    private Lobby lobby;
    private Connection connection;
    private Reader reader;
    private Game game = new Game();
    private Parser parser;

    public Controller(Connection connection) {
        reader = new Reader(connection);
        new Thread(reader).run();
        reader.request(this);
        this.connection = connection;
    }

    private int write(Message message) throws DisconnectedException {
        byte[] bytes = message.toBytes();
        return connection.write(bytes);
    }

    @Override
    protected void consumed(Producer<Message> sender, Message message) {
        if(sender == reader) {
            if (message.getCommand() == Command.DISCONNECTED) {
                reader.stop(this);
            } else {
                game.update(message);
                switch (message.getCommand()) {
                    case HELLO:
                        lobby = new Lobby(game.isLeader(), game.size());
                        lobby.setLocationRelativeTo(null);
                        lobby.setVisible(true);
                }
                reader.request(this);
            }
        } else {
            try {
                write(message);
            } catch(DisconnectedException ex) {
                reader.stop(this);
            }
        }
    }

    @Override
    protected void stopped(Producer<Message> by) {
        reader.stop(this);
    }

    @Override
    protected void interrupted() {
        // Should never happen
        assert false;
    }
}
