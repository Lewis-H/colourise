package colourise.bot;

import colourise.networking.Connection;
import colourise.networking.DisconnectedException;
import colourise.networking.protocol.Card;
import colourise.networking.protocol.Message;
import colourise.networking.protocol.Parser;
import colourise.state.match.*;
import colourise.state.match.CardAlreadyUsedException;
import colourise.state.match.Player;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Bot connection and logic.
 */
public class Bot {
    // The bot's connection
    private Connection connection;
    // Count of lobby connections
    private int lobby = 0;
    // Whether this bot is the lobby's leader
    private boolean leader = false;
    // The match the bot is playing
    private Match match;
    // The bot's player
    private Player me;
    // Packet parser
    private final Parser parser = new Parser();
    // RNG
    private final Random random = new Random();

    /**
     * Bot constructor.
     * @param connection The connection to use.
     */
    public Bot(Connection connection) {
        this.connection = connection;
    }

    /**
     * Starts playing.
     * @throws CardAlreadyUsedException
     * @throws MatchFinishedException
     * @throws NotPlayersTurnException
     * @throws CannotPlayException
     * @throws InvalidPositionException
     * @throws DisconnectedException
     */
    public void start() throws CardAlreadyUsedException, MatchFinishedException, NotPlayersTurnException, CannotPlayException, InvalidPositionException, DisconnectedException {
        // Hello to server
        write(Message.Factory.hello(false));
        while(true) {
            while (parser.getRemaining() != 0)
                parser.add(connection.read(parser.getRemaining()));
            process(parser.create());
            parser.reset();
        }
    }

    /**
     * Processes a received packet.
     * @param message The parsed packet
     * @throws CardAlreadyUsedException
     * @throws MatchFinishedException
     * @throws NotPlayersTurnException
     * @throws CannotPlayException
     * @throws InvalidPositionException
     * @throws DisconnectedException
     */
    private void process(Message message) throws CardAlreadyUsedException, MatchFinishedException, NotPlayersTurnException, CannotPlayException, InvalidPositionException, DisconnectedException {
        if(lobby == 0 && match == null) {
            switch(message.getCommand()) {
                // Joined the lobby
                case JOINED:
                    lobby = message.getArgument(0); // Count of players
                    break;
            }
        } else if(lobby != 0) {
            switch(message.getCommand()) {
                case LEAD:
                    leader = true; // This bot is the leader
                    break;
                case JOINED:
                    ++lobby;
                    break;
                case LEFT:
                    --lobby;
                    break;
                case BEGIN:
                    // Starts the match
                    lobby = 0;
                    int id = message.getArgument(0);
                    int count = message.getArgument(1);
                    // Replicate starting positions
                    Map<Integer, Position> starts = new HashMap<>(count);
                    for(int i = 0; i < count; i++)
                        starts.put(i, new Position(message.getArgument(2 + 2 * i), message.getArgument(3 + 2 * i)));
                    match = new Match(starts);
                    for(Player player : match.getPlayers()) {
                        if(player.getIdentifier() == id) {
                            me = player;
                            break;
                        }
                    }
                    // 0 always plays first
                    if(id == 0)
                        play();
                    break;
            }
        } else if(match != null) { // Play in the match
            int id;
            int next;
            switch(message.getCommand()) {
                // A player has played a position, add this to the game state
                case PLAYED:
                    id = message.getArgument(0);
                    int row = message.getArgument(1);
                    int column = message.getArgument(2);
                    Card card = Card.fromInt(message.getArgument(3));
                    next = message.getArgument(4);
                    for(Player player : match.getPlayers())
                        if (player.getIdentifier() == id)
                            player.play(row, column, card);
                    if(next == me.getIdentifier())
                        play();
                    break;
                // A player left the match, record this in the game state
                case LEFT:
                    id = message.getArgument(0);
                    next = message.getArgument(1);
                    for(Player player : match.getPlayers())
                        if(player.getIdentifier() == id)
                            player.leave();
                    if(next == me.getIdentifier())
                        play();
                    break;
            }
        }
    }

    private void play() throws DisconnectedException {
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch(InterruptedException ex) {
        } finally {
            List<Position> positions = null;
            Card card = Card.NONE;
            // Try playing the freedom card first
            if(me.has(Card.FREEDOM)) {
                positions = Searcher.freedom(match);
                card = Card.FREEDOM;
            }
            // If freedom can't be played, play replacement
            if(me.has(Card.REPLACEMENT) && (positions == null || positions.size() == 0)) {
                positions = Searcher.replacement(match, me);
                card = Card.REPLACEMENT;
            }
            // If no cards can be played, play a regular move
            if(positions == null || positions.size() == 0) {
                positions = Searcher.all(match, me);
                card = Card.NONE;
            }
            // Select a random choice
            Position position = positions.get(random.nextInt(positions.size()));
            // Write the move to the server. State will be updated when the packet returns.
            write(Message.Factory.play(position.getRow(), position.getColumn(), card));
        }
    }

    /**
     * Sends a packet over the connection
     * @param m Message to send
     * @return Bytes written
     * @throws DisconnectedException
     */
    private int write(Message m) throws DisconnectedException {
        return connection.write(m.toBytes());
    }


}
