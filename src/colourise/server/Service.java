package colourise.server;

import colourise.networking.*;
import colourise.networking.protocol.*;
import colourise.networking.protocol.Error;
import colourise.state.lobby.Lobby;
import colourise.state.lobby.LobbyFullException;
import colourise.state.match.*;
import colourise.state.player.CardAlreadyUsedException;
import colourise.state.player.Player;
import colourise.synchronisation.Consumer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.*;

public class Service implements Listener {
    private final Map<Connection, Player> players = new HashMap<>();
    private final Map<Player, Connection> connections = new HashMap<>();
    private final Map<Connection, Parser> parsers = new HashMap<>();
    private Lobby<Connection> lobby;
    private Server server;

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
    public void connected(Connection c) {
        if(c == null)
            throw new IllegalArgumentException("c");
        System.out.println("Client connected");
        parsers.put(c, new Parser());
        join(c);
    }

    private void join(Connection connection) {
        if(connection == null)
            throw new IllegalArgumentException("connection");
        try {
            write(lobby, Message.Factory.joined(lobby.size() + 1));
            lobby.join(connection);
            if(lobby.size() == lobby.capacity())
                throw new LobbyFullException(lobby);
            write(connection, Message.Factory.hello(connection == lobby.getLeader(), lobby.size()));
        } catch(LobbyFullException ex) {
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
        }
        parsers.remove(connection);
    }

    private void left(Connection connection, Player player) throws MatchFinishedException {
        // Remove player object
        players.remove(connection);
        // Notify remaining players
        write(player.getMatch(), Message.Factory.left(player.getIdentifier(), player.getMatch().getCurrent().getIdentifier()));
        // Leave the match
        player.leave();
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
            }
        } catch(DisconnectedException ex) {
            disconnected(connection);
        } catch(ArrayIndexOutOfBoundsException ex) {
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
        try {
            Match match = player.getMatch();
            switch(message.getCommand()) {
                case LEAVE:
                    // Join lobby
                    join(connection);
                    left(connection, player);
                    break;
                case PLAY:
                    try {
                        Card card = Card.fromInt(message.getArgument(2));
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
                    }
                    break;
                default:
                    // Unrecognised command
                    return;
            }
        } catch(MatchFinishedException ex) {
            finished(ex.getMatch());
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
            disconnected(connection);
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
        Message[] starts = new Message[match.getStarts().size()];
        int i = 0;
        for(Start start : match.getStarts())
            starts[i++] = Message.Factory.played(start.getPlayer().getIdentifier(), start.getRow(), start.getColumn(), Card.NONE, match.getCurrent().getIdentifier());
        while(i1.hasNext() && i2.hasNext()) {
            Player player = i1.next();
            Connection connection = i2.next();
            players.put(connection, player);
            connections.put(player, connection);
            write(connection, Message.Factory.begin(player.getIdentifier(), match.getPlayers().size()));
            for(Message start : starts)
                write(connection, start);
        }
    }

    public void finished(Match match) {
        if(match == null)
            throw new IllegalArgumentException("match");
        int[] scores = new int[5];
        int i = 0;
        for(Integer s : match.getScoreboard().values())
            scores[i++] = s;
        write(match, Message.Factory.end(scores[0], scores[1], scores[2], scores[3], scores[4]));
        for(Player player : match.getPlayers())
            players.remove(connectionOf(player));
    }
}
