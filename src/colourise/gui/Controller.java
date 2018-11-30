package colourise.gui;

import colourise.networking.Connection;
import colourise.networking.DisconnectedException;
import colourise.networking.protocol.Error;
import colourise.networking.protocol.Message;
import colourise.networking.protocol.Parser;
import colourise.state.match.Position;
import colourise.synchronisation.Consumer;
import colourise.synchronisation.Producer;
import colourise.synchronisation.RunnableConsumer;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller, provides some window flow control as the stages (connect, lobby, match) progress.
 */
public class Controller extends RunnableConsumer<Message> {
    // Lobby frame
    private Lobby lobby;
    // Board/game frame
    private Board board;
    // Connection
    private Connection connection;
    // Connection reader (Producer)
    private Reader reader;
    // Consumer to forward messages to
    private Consumer<Message> forward = null;
    // Whether this player is a spectator
    private final boolean spectate;

    /**
     * Controller constructor.
     * @param connection Connection
     * @param spectate Whether this connection is spectate only
     * @throws DisconnectedException
     */
    public Controller(Connection connection, boolean spectate) throws DisconnectedException {
        new Thread(reader = new Reader(connection)).start();
        reader.request(this);
        this.connection = connection;
        write(Message.Factory.hello(this.spectate = spectate));
    }

    /**
     * Writes a message over the connection.
     * @param message Message to write
     * @return Bytes written
     * @throws DisconnectedException
     */
    private int write(Message message) throws DisconnectedException {
        byte[] bytes = message.toBytes();
        return connection.write(bytes);
    }

    /**
     * Processes the messages recieved from producers
     * @param sender Producer which sent the message
     * @param message Message
     */
    @Override
    protected void consumed(Producer<Message> sender, Message message) {
        if(sender == reader) {
            switch (message.getCommand()) {
                // HELLO acknowledged, joined the lobby
                case JOINED:
                    if(lobby == null) {
                        lobby = new Lobby(message.getArgument(0));
                        lobby.request(this);
                        lobby.setLocationRelativeTo(null);
                        lobby.setVisible(true);
                        forward = lobby;
                    }
                    break;
                // Game is starting
                case BEGIN:
                    lobby.push(sender, message);
                    int count = message.getArgument(1);
                    Map<Integer, Position> positions = new HashMap<Integer, Position>(count);
                    for(int i = 0; i < count; i++)
                        positions.put(i, new Position(message.getArgument(2 + 2 * i), message.getArgument(3 + 2 * i)));
                    board = new Board(25, message.getArgument(0), message.getArgument(1), positions, spectate);
                    board.request(this);
                    board.setVisible(true);
                    forward = board;
                    break;
                // Something happened...
                case ERROR:
                    System.out.println(Error.fromInt((int) message.getArgument(0)));
                    break;
            }
            // Forward the message
            if(forward != null)
                forward.push(sender, message);
            // Request another
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

    /**
     * Processes instruction to stop
     * @param by
     */
    @Override
    protected void stopped(Producer<Message> by) {
        // Stops the reader
        reader.stop(this);
        // Disconnects
        connection.disconnect();
    }

    @Override
    protected void interrupted() {
        // Should never happen
        assert false;
    }
}
