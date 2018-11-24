package colourise.server;

import colourise.networking.*;
import colourise.networking.protocol.*;
import colourise.server.lobby.Lobby;
import colourise.server.lobby.MatchStartedException;
import colourise.server.match.*;
import colourise.server.player.CardAlreadyUsedException;
import colourise.server.player.Player;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

public class Service implements Listener {
    private final Map<Connection, Player> players = new HashMap<>();
    private final Map<Connection, Parser> parsers = new HashMap<>();
    private Lobby lobby;
    private Server server;

    public Service(InetSocketAddress address) throws IOException {
        if(address == null)
            throw new IllegalArgumentException("address");
        lobby = new Lobby();
        server = Binder.listen(address, this);
    }

    public void listen() throws IOException {
        server.listen();
    }

    private Parser parser(Connection c) {
        if(c == null)
            throw new IllegalArgumentException("c");
        return parsers.get(c);
    }

    @Override
    public void connected(Connection c) {
        if(c == null)
            throw new IllegalArgumentException("c");
        System.out.println("Client connected");
        parsers.put(c, new Parser());
        join(c);
    }

    private void join(Connection c) {
        if(c == null)
            throw new IllegalArgumentException("c");
        try {
            write(lobby, Message.Factory.joined(lobby.count() + 1));
            lobby.join(c);
            write(c, Message.Factory.hello(c == lobby.getLeader(), lobby.count()));
        } catch(MatchStartedException ex) {
            started(ex.getMatch());
        }
    }

    @Override
    public void disconnected(Connection c) {
        if(c == null)
            throw new IllegalArgumentException("c");
        Player player = players.get(c);
        if(player != null) { // Player is in a game.
            try {
                player.leave();
            } catch(MatchFinishedException ex) {
                finished(ex.getMatch());
            }
            players.remove(c);
            write(player.getMatch(), Message.Factory.left(player.getIdentifier()));
        } else { // Player is in the lobby.
            lobby.leave(c);
            write(lobby, Message.Factory.left(lobby.count()));
        }
        parsers.remove(c);
    }

    @Override
    public void read(Connection c) {
        if(c == null)
            throw new IllegalArgumentException("c");
        try {
            parser(c).add(c.read(parser(c).getRemaining()));
            if (parser(c).getRemaining() == 0) {
                Player p = players.get(c);
                if (p != null) {
                    received(p, parser(c).create());
                    parser(c).reset();
                }
            }
        } catch(DisconnectedException ex) {
            disconnected(c);
        }
    }

    private void received(Player p, Message m) {
        if(p == null)
            throw new IllegalArgumentException("p");
        if(m == null)
            throw new IllegalArgumentException("m");
        try {
            Match match = p.getMatch();
            switch(m.getCommand()) {
                case LEAVE:
                    // Leave the match
                    p.leave();
                    // Notify remaining players
                    write(match, Message.Factory.left(p.getIdentifier()));
                    // Remove player object
                    players.remove(p.getConnection());
                    // Join lobby
                    join(p.getConnection());
                    break;
                case PLAY:
                    try {
                        p.play(m.getArgument(0), m.getArgument(1), Card.fromInt(m.getArgument(2)));
                        write(match, Message.Factory.played(p.getIdentifier(), m.getArgument(0), m.getArgument(1)));
                    } catch(NotPlayersTurnException | InvalidPositionException | CannotPlayException | CardAlreadyUsedException ex) {
                        return;
                    }
                    break;
                default:
                    // Unrecognised command
                    return;
            }
        } catch (MatchFinishedException ex) {
            finished(ex.getMatch());
        }
    }

    private int write(Connection c, byte[] b) {
        if(c == null)
            throw new IllegalArgumentException("c");
        if(b == null)
            throw new IllegalArgumentException("b");
        try {
            return c.write(b);
        }catch(DisconnectedException ex) {
            disconnected(c);
            return 0;
        }
    }

    private int write(Connection c, Message m) {
        if(c == null)
            throw new IllegalArgumentException("c");
        if(m == null)
            throw new IllegalArgumentException("m");
        return write(c, m.toBytes());
    }

    private void write(Lobby l, Message m) {
        if(l == null)
            throw new IllegalArgumentException("l");
        if(m == null)
            throw new IllegalArgumentException("m");
        byte[] bytes = m.toBytes();
        for(Connection c : l.getConnections())
            write(c, bytes);
    }

    private void write(Match match, Message m) {
        if(match == null)
            throw new IllegalArgumentException("match");
        if(m == null)
            throw new IllegalArgumentException("m");
        byte[] bytes = m.toBytes();
        for(Player p : match.getPlayers())
            write(p.getConnection(), bytes);
    }

    public void started(Match match) {
        if(match == null)
            throw new IllegalArgumentException("match");
        for(Player player : match.getPlayers()) {
            players.put(player.getConnection(), player);
            if(parser(player.getConnection()).getRemaining() == 0) {
                received(player, parser(player.getConnection()).create());
                parser(player.getConnection()).reset();
            }
            write(player.getConnection(), Message.Factory.begin(player.getIdentifier(), match.getPlayers().size()));
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
            players.remove(player.getConnection());
    }
}
