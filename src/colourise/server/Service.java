package colourise.server;

import colourise.ColouriseException;
import colourise.networking.*;
import colourise.networking.protocol.*;
import colourise.networking.protocol.Error;
import colourise.state.lobby.Lobby;
import colourise.state.lobby.LobbyFullException;
import colourise.state.match.*;
import colourise.state.player.CardAlreadyUsedException;
import colourise.state.player.Player;
import javafx.geometry.Pos;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.*;

public class Service implements Listener {
    private final Map<Connection, Player> players = new HashMap<>();
    private final Map<Player, Connection> connections = new HashMap<>();
    private final Map<Connection, Parser> parsers = new HashMap<>();
    private final Lobby<Connection> lobby;
    private final Server server;
    private final Set<Connection> disconnected = new HashSet<>();

    public Service(InetSocketAddress address) throws IOException {
        if(address == null)
            throw new IllegalArgumentException("address");
        lobby = new Lobby<>(Match.MAX_PLAYERS);
        server = Binder.listen(address, this);
    }

    public void listen() throws IOException {
        server.listen();
    }

    private Parser parserOf(Connection connection) {
        if(connection == null)
            throw new IllegalArgumentException("connection");
        return parsers.get(connection);
    }

    private Connection connectionOf(Player player) {
        if(player == null)
            throw new IllegalArgumentException("player");
        return connections.get(player);
    }

    private Player playerOf(Connection connection) {
        if(connection == null)
            throw new IllegalArgumentException("connection");
        return players.get(connection);
    }

    @Override
    public void connected(Connection connection) {
        if(connection == null)
            throw new IllegalArgumentException("connection");
        System.out.println("Client connected");
        parsers.put(connection, new Parser());
        join(connection);
    }

    private void join(Connection connection) {
        if(connection == null)
            throw new IllegalArgumentException("connection");
        try {
            write(lobby, Message.Factory.joined(lobby.size() + 1));
            lobby.join(connection);
            if(lobby.size() == lobby.capacity())
                throw new LobbyFullException(lobby);
            write(connection, Message.Factory.hello(lobby.size()));
            if(lobby.getLeader() == connection)
                write(connection, Message.Factory.lead());
        } catch(LobbyFullException ex) {
            write(connection, Message.Factory.hello(lobby.size()));
            if(lobby.getLeader() == connection)
                write(connection, Message.Factory.lead());
            started(lobby);
            lobby.clear();
        }
    }

    @Override
    public void disconnected(Connection connection) {
        if(connection == null)
            throw new IllegalArgumentException("connection");
        System.out.println("Client disconnected");
        Player player = playerOf(connection);
        if(player != null) { // Player is in a game.
            try {
                left(connection, player);
            } catch(MatchFinishedException ex) {
                finished(ex.getMatch());
            }
        } else { // Player is in the lobby.
            lobby.leave(connection);
            write(lobby, Message.Factory.left(lobby.size(), 0));
            if(lobby.getLeader() != null)
                write(lobby.getLeader(), Message.Factory.lead());
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
        if(connection == null)
            throw new IllegalArgumentException("connection");
        try {
            parserOf(connection).add(connection.read(parserOf(connection).getRemaining()));
            if (parserOf(connection).getRemaining() == 0) {
                Player p = players.get(connection);
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
        switch(message.getCommand()) {
            case START:
                if(connection == lobby.getLeader())
                    started(lobby);
                break;
        }
    }

    private void received(Player player, Message message) {
        if(player == null)
            throw new IllegalArgumentException("player");
        if(message == null)
            throw new IllegalArgumentException("message");
        Connection connection = connectionOf(player);
        Match match = player.getMatch();
        switch(message.getCommand()) {
            case PLAY:
                Card card = Card.fromInt(message.getArgument(2));
                try {

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
        byte[] bytes = message.toBytes();
        for(Connection connection : lobby.getWaiters())
            write(connection, bytes);
    }

    private void write(Match match, Message message) {
        if(match == null)
            throw new IllegalArgumentException("match");
        if(message == null)
            throw new IllegalArgumentException("message");
        byte[] bytes = message.toBytes();
        for(Player player : match.getPlayers())
            write(connectionOf(player), bytes);
    }

    public void started(Lobby<Connection> lobby) {
        if(lobby == null)
            throw new IllegalArgumentException("connections");
        Match match = new Match(lobby.size());
        Iterator<Player> i1 = match.getPlayers().iterator();
        Iterator<Connection> i2 = lobby.getWaiters().iterator();
        int[] rows = new int[5];
        int[] columns = new int[5];
        for(Map.Entry<Integer, Position> start : match.getStarts().entrySet()) {
            rows[start.getKey()] = start.getValue().getRow();
            columns[start.getKey()] = start.getValue().getColumn();
        }
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
    }

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
        }
    }
}
