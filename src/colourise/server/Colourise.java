package colourise.server;

import colourise.networking.Binder;
import colourise.networking.Connection;
import colourise.networking.Listener;
import colourise.networking.Server;
import colourise.networking.protocol.*;
import colourise.server.lobby.Lobby;
import colourise.server.lobby.MatchStartedException;
import colourise.server.match.Match;
import colourise.server.match.MatchFinishedException;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

public class Colourise implements Listener {
    private final Map<Connection, Player> players = new HashMap<>();
    private final Map<Connection, Parser> parsers = new HashMap<>();
    private Lobby lobby;
    private Server server;

    public Colourise(InetSocketAddress address) throws IOException {
        lobby = new Lobby();
        server = Binder.listen(address, this);
    }

    public void listen() throws IOException {
        server.listen();
    }

    private Parser parser(Connection c) {
        return parsers.get(c);
    }

    @Override
    public void connected(Connection c) {
        parsers.put(c, new Parser());
        join(c);
    }

    public void join(Connection c) {
        try {
            lobby.join(c);
            write(c, Message.Factory.hello(c == lobby.getLeader()));
            lobby.write(Message.Factory.joined(lobby.count()));
        } catch(MatchStartedException ex) {
            started(ex.getMatch());
        }
    }

    @Override
    public void disconnected(Connection c) {
        Player player = players.get(c);
        if(player != null) { // Player is in a game.
            try {
                player.leave();
            } catch(MatchFinishedException ex) {
                finished(ex.getMatch());
            }
            players.remove(c);
            player.getMatch().write(Message.Factory.left(player.getIdentifier()));
        } else { // Player is in the lobby.
            lobby.leave(c);
            lobby.write(Message.Factory.left(lobby.count()));
        }
        parsers.remove(c);
    }

    @Override
    public void read(Connection c) {
        parser(c).add(c.read(parser(c).getRemaining()));
        if(parser(c).getRemaining() == 0) {
            Player p = players.get(c);
            if(p != null) {
                received(p, parser(c).create());
                parser(c).reset();
            }
        }
    }

    private void received(Player p, Message m) {
        try {
            switch(m.getCommand()) {
                case LEAVE:
                    p.leave();
                    p.getMatch().write(Message.Factory.left(p.getIdentifier()));
                    players.remove(p.getConnection());
                    join(p.getConnection());
                    break;
                case PLAY:
                    p.play(m.getArgument(0), m.getArgument(1), Card.fromInt(m.getArgument(2)));
                    p.getMatch().write(Message.Factory.played(p.getIdentifier(), m.getArgument(0), m.getArgument(1)));
                    break;
                default:
                    // Unrecognised command
                    return;
            }
        } catch (MatchFinishedException ex) {
            finished(ex.getMatch());
        }
    }

    public int write(Connection c, Message m) {
        return c.write(m.toBytes());
    }

    public void started(Match match) {
        for(Player player : match.getPlayers()) {
            players.put(player.getConnection(), player);
            if(parser(player.getConnection()).getRemaining() == 0) {
                received(player, parser(player.getConnection()).create());
                parser(player.getConnection()).reset();
            }
        }
    }

    public void finished(Match match) {
        int[] scores = new int[5];
        int i = 0;
        for(Integer s : match.getScoreboard().values())
            scores[i++] = s;
        match.write(Message.Factory.end(scores[0], scores[1], scores[2], scores[3], scores[4]));
        for(Player player : match.getPlayers())
            players.remove(player.getConnection());
    }
}
