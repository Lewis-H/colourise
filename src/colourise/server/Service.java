package colourise.server;

import colourise.networking.*;
import colourise.networking.protocol.*;
import colourise.networking.protocol.Error;
import colourise.state.lobby.Lobby;
import colourise.state.lobby.LobbyFullException;
import colourise.state.match.*;
import colourise.state.match.CardAlreadyUsedException;
import colourise.state.match.Player;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.*;

/**
 * Game server service.
 */
public class Service implements Listener {
    // Map of connections to players
    private final Map<Connection, Player> players = new HashMap<>();
    // Map of players to connections
    private final Map<Player, Connection> connections = new HashMap<>();
    // Map of connections to parsers
    private final Map<Connection, Parser> parsers = new HashMap<>();
    // The lobby
    private final Lobby<Connection> lobby;
    // The underlying server
    private final Server server;
    // Set of connections to be removed after the next read loop is complete
    private final Set<Connection> disconnected = new HashSet<>();
    // Set of joining specators
    private final Set<Connection> spectate = new HashSet<>();
    // Map of matches to connections which are spectating it
    private final Map<Match, Set<Connection>> spectated = new HashMap<>();
    // Map of connections and the matches they are spectating
    private final Map<Connection, Match> spectating = new HashMap<>();

    /**
     * Initialises the servive.
     * @param address Bind address
     * @throws IOException
     */
    public Service(InetSocketAddress address) throws IOException {
        if(address == null)
            throw new IllegalArgumentException("address");
        lobby = new Lobby<>(Match.MAX_PLAYERS);
        server = Binder.listen(address, this);
    }

    /**
     * Listens for requests
     * @throws IOException
     */
    public void listen() throws IOException {
        server.listen();
    }

    /**
     * Gets the parser of the specified connection
     * @param connection Connection
     * @return
     */
    private Parser parserOf(Connection connection) {
        if(connection == null)
            throw new IllegalArgumentException("connection");
        return parsers.get(connection);
    }

    /**
     * Gets the connection of the specifier player
     * @param player Player
     * @return
     */
    private Connection connectionOf(Player player) {
        if(player == null)
            throw new IllegalArgumentException("player");
        return connections.get(player);
    }

    /**
     * Gets the player of the specified connection
     * @param connection Connection
     * @return
     */
    private Player playerOf(Connection connection) {
        if(connection == null)
            throw new IllegalArgumentException("connection");
        return players.get(connection);
    }

    /**
     * Gets the spectators of the specified match
     * @param match Match
     * @return
     */
    private Set<Connection> spectatorsOf(Match match) {
        if(match == null)
            throw new IllegalArgumentException("match");
        return spectated.get(match);
    }

    @Override
    public void connected(Connection connection) {
        // New connection
        if(connection == null)
            throw new IllegalArgumentException("connection");
        parsers.put(connection, new Parser());
    }

    private void join(Connection connection, boolean spectator) {
        // Joining the lobby
        if(connection == null)
            throw new IllegalArgumentException("connection");
        try {
            if(spectator) {
                // Set up as spectator
                spectate.add(connection);
                write(connection, Message.Factory.joined(lobby.size()));
            } else {
                // Join the lobby
                lobby.join(connection);
                Message message = Message.Factory.joined(lobby.size());
                write(lobby, message);
                write(spectate, message);
                if (lobby.getLeader() == connection)
                    write(connection, Message.Factory.lead());
                if (lobby.size() == lobby.capacity())
                    started(lobby, spectate); // Start match
            }
        } catch(LobbyFullException ex) {
            // Shouldn't happen as size and capacity are checked.
            started(lobby, spectate);
            join(connection, spectator);
        }
    }

    @Override
    public void disconnected(Connection connection) {
        // Connection has disconnected
        if(connection == null)
            throw new IllegalArgumentException("connection");
        Player player = playerOf(connection);
        // Tidy up objects in maps/lists/sets
        if(player != null) { // Player is in a game.
            try {
                left(connection, player);
            } catch(MatchFinishedException ex) {
                finished(ex.getMatch());
            }
        } else if(lobby.leave(connection)) { // Player is in a lobby
            write(lobby, Message.Factory.left(lobby.size(), 0));
            if (lobby.getLeader() != null)
                write(lobby.getLeader(), Message.Factory.lead());
        } else {
            Match match = spectating.get(connection);
            if(match != null) {
                spectating.remove(connection);
                spectated.get(match).remove(connection);
            } else {
                spectate.remove(connection);
            }
        }
        parsers.remove(connection);
    }

    private void left(Connection connection, Player player) throws MatchFinishedException {
        // Remove player object
        players.remove(connection);
        // Leave the match
        player.leave();
        // Notify remaining players
        write(player.getMatch(), Message.Factory.left(player.getIdentifier(), player.getMatch().getCurrent().getIdentifier()));
    }

    @Override
    public void read(Connection connection) {
        // Data is available to be read on the connection
        if(connection == null)
            throw new IllegalArgumentException("connection");
        try {
            // Read to the parser until no more bytes are required
            parserOf(connection).add(connection.read(parserOf(connection).getRemaining()));
            if (parserOf(connection).getRemaining() == 0) {
                Player p = players.get(connection);
                // Process the packet
                if (p != null)
                    received(p, parserOf(connection).create());
                else
                    received(connection, parserOf(connection).create());
                parserOf(connection).reset();
                for(Connection disconnect : disconnected)
                    disconnected(disconnect);
                disconnected.clear();
            }
        } catch(DisconnectedException ex) {
            disconnected(connection);
        } catch(ArrayIndexOutOfBoundsException ex) {
            ex.printStackTrace();
            connection.disconnect();
            disconnected(connection);
        }
    }

    private void received(Connection connection, Message message) {
        // Message handler, no player
        switch(message.getCommand()) {
            case HELLO: // First packet
                join(connection, message.getArgument(0) == 1);
                break;
            case START: // Starts the game
                if(connection == lobby.getLeader())
                    started(lobby, spectate);
                break;
        }
    }

    private void received(Player player, Message message) {
        // Message handler, with player
        if(player == null)
            throw new IllegalArgumentException("player");
        if(message == null)
            throw new IllegalArgumentException("message");
        Connection connection = connectionOf(player);
        Match match = player.getMatch();
        switch(message.getCommand()) {
            case PLAY: // Player has played a position
                Card card = Card.fromInt(message.getArgument(2));
                try {
                    // Update game state
                    player.play(message.getArgument(0), message.getArgument(1), card);
                    write(match, Message.Factory.played(player.getIdentifier(), message.getArgument(0), message.getArgument(1), card, match.getCurrent().getIdentifier()));
                } catch (NotPlayersTurnException ex) {
                    write(connection, Message.Factory.error(Error.NOT_PLAYERS_TURN));
                } catch (InvalidPositionException ex) {
                    write(connection, Message.Factory.error(Error.INVALID_POSITION));
                } catch (CannotPlayException ex) {
                    write(connection, Message.Factory.error(Error.CANNOT_PLAY));
                }catch(CardAlreadyUsedException ex) {
                    write(connection, Message.Factory.error(Error.CARD_ALREADY_USED));
                } catch(MatchFinishedException ex) {
                    write(match, Message.Factory.played(player.getIdentifier(), message.getArgument(0), message.getArgument(1), card, match.getCurrent().getIdentifier()));
                    finished(ex.getMatch());
                }
                break;
            default:
                // Unrecognised command
                return;
        }
    }

    // Various write commands to avoid repetition
    private int write(Connection connection, byte[] bytes) {
        if(connection == null)
            throw new IllegalArgumentException("connection");
        if(bytes == null)
            throw new IllegalArgumentException("bytes");
        try {
            return connection.write(bytes);
        }catch(DisconnectedException ex) {
            disconnected.add(ex.getConnection());
            return 0;
        }
    }

    private int write(Connection connection, Message message) {
        if(connection == null)
            throw new IllegalArgumentException("connection");
        if(message == null)
            throw new IllegalArgumentException("message");
        return write(connection, message.toBytes());
    }

    private void write(Lobby<Connection> lobby, Message message) {
        if(lobby == null)
            throw new IllegalArgumentException("lobby");
        if(message == null)
            throw new IllegalArgumentException("message");
        write(lobby.getWaiters(), message);
    }

    private void write(Collection<Connection> connections, byte[] bytes) {
        if(connections == null)
            throw new IllegalArgumentException("connections");
        if(bytes == null)
            throw new IllegalArgumentException("bytes");
        for(Connection connection : connections)
            write(connection, bytes);
    }

    private void write(Collection<Connection> connections, Message message) {
        if(connections == null)
            throw new IllegalArgumentException("connections");
        if(message == null)
            throw new IllegalArgumentException("message");
        byte[] bytes = message.toBytes();
        write(connections, bytes);
    }

    private void write(Match match, Message message) {
        if(match == null)
            throw new IllegalArgumentException("match");
        if(message == null)
            throw new IllegalArgumentException("message");
        byte[] bytes = message.toBytes();
        for(Player player : match.getPlayers())
            write(connectionOf(player), bytes);
        write(spectatorsOf(match), bytes);
    }

    /**
     * Starts a match from a lobby
     * @param lobby
     * @param spectators
     */
    public void started(Lobby<Connection> lobby, Set<Connection> spectators) {
        if(lobby == null)
            throw new IllegalArgumentException("connections");
        // Create match
        Match match = new Match(lobby.size());
        // Prepare spectators
        spectated.put(match, new HashSet<>(spectators));
        for(Connection connection : spectators)
            spectating.put(connection, match);
        // Prepare connection/player pairing
        Iterator<Player> i1 = match.getPlayers().iterator();
        Iterator<Connection> i2 = lobby.getWaiters().iterator();
        // Prepare starting positions for sending
        int[] rows = new int[5];
        int[] columns = new int[5];
        for(Map.Entry<Integer, Position> start : match.getStarts().entrySet()) {
            rows[start.getKey()] = start.getValue().getRow();
            columns[start.getKey()] = start.getValue().getColumn();
        }
        // Send starting positions and populate maps
        while(i1.hasNext() && i2.hasNext()) {
            Player player = i1.next();
            Connection connection = i2.next();
            players.put(connection, player);
            connections.put(player, connection);
            write(connection, Message.Factory.begin(
                    player.getIdentifier(),
                    match.getPlayers().size(),
                    rows[0],
                    columns[0],
                    rows[1],
                    columns[1],
                    rows[2],
                    columns[2],
                    rows[3],
                    columns[3],
                    rows[4],
                    columns[4]
            ));
        }
        write(spectators, Message.Factory.begin(
                match.getPlayers().size(), // ID not in use, will never be selected to play
                match.getPlayers().size(),
                rows[0],
                columns[0],
                rows[1],
                columns[1],
                rows[2],
                columns[2],
                rows[3],
                columns[3],
                rows[4],
                columns[4]
        ));
        // Clear lobby collections
        spectators.clear();
        lobby.clear();
    }

    /**
     * Cleans up a match after it has finished
     * @param match Match which has finished
     */
    public void finished(Match match) {
        if(match == null)
            throw new IllegalArgumentException("match");
        int[] scores = new int[5];
        for(Map.Entry<Player, Integer> score : match.getScoreboard().entrySet())
            scores[score.getKey().getIdentifier()] = score.getValue();
        write(match, Message.Factory.end(scores[0], scores[1], scores[2], scores[3], scores[4]));
        for(Player player : match.getPlayers()) {
            Connection connection = connectionOf(player);
            players.remove(connection);
            connection.disconnect();
            disconnected.add(connection);
        }
    }
}
