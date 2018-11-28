package colourise.gui;

import colourise.client.*;
import colourise.networking.Connection;
import colourise.networking.DisconnectedException;
import colourise.networking.protocol.Command;
import colourise.networking.protocol.Error;
import colourise.networking.protocol.Message;
import colourise.networking.protocol.Parser;
import colourise.synchronisation.Consumer;
import colourise.synchronisation.Producer;
import colourise.synchronisation.RunnableConsumer;

public class Controller extends RunnableConsumer<Message> {
    private Lobby lobby;
    private Board board;
    private Connection connection;
    private Reader reader;
    private Parser parser;
    private Consumer<Message> forward = null;

    public Controller(Connection connection) {
        new Thread(reader = new Reader(connection)).start();
        reader.request(this);
        this.connection = connection;
    }

    private int write(Message message) throws DisconnectedException {
        byte[] bytes = message.toBytes();
        return connection.write(bytes);
    }

    @Override
    protected void consumed(Producer<Message> sender, Message message) {
        System.out.println(message.getCommand());
        if(sender == reader) {
            switch (message.getCommand()) {
                case HELLO:
                    lobby = new Lobby(message.getArgument(0));
                    lobby.request(this);
                    lobby.setLocationRelativeTo(null);
                    lobby.setVisible(true);
                    forward = lobby;
                    break;
                case BEGIN:
                    lobby.push(sender, message);
                    board = new Board(25, message.getArgument(0), message.getArgument(1));
                    board.request(this);
                    board.setVisible(true);
                    forward = board;
                    break;
                case ERROR:
                    System.out.println(Error.fromInt((int) message.getArgument(0)));
                    break;
            }
            if(forward != null)
                forward.push(sender, message);
            reader.request(this);
        } else {
            try {
                sender.request(this);
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
