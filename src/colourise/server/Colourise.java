package colourise.server;

import colourise.networking.Binder;
import colourise.networking.Connection;
import colourise.networking.Listener;
import colourise.networking.Server;
import colourise.networking.protocol.Message;

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
        lobby = new Lobby(this);
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
        write(new Message(Command.JOINED, new byte[] { lobby.count() + 1 }));
        game.write(new Message(Command.HELLO, new byte[] { connection == leader, lobby.count() + 1 }));
        lobby.join(c);
    }

    @Override
    public void disconnected(Connection c) {
        Player player = players.get(c);
        if(player != null) { // Player is in a game.
            player.leave();
            player.getMatch().write(new Message(Command.LEFT, new byte[] { player.getIdentifier() }));
        } else { // Player is in the lobby.
            lobby.leave(c);
            lobby.write(new Message(Command.LEFT, new byte[] { lobby.count() }));
        }
        parsers.remove(c);
    }

    @Override
    public void read(Connection c) {
        byte[] bytes = c.read(parser(c).getRemaining());
        if(parser(c).getRemaining() == 0) {
            Player p = players.get(c);
            Message m = parser(c).create();
            if(p != null)
                received(p, m);
            else
                received(c, m);
            parser(c).reset();
        }
    }

    private void received(Connection c, Message m) {
        return; // Implement
    }

    private void received(Player p, Message m) {
        switch(m.getCommand()) {
            case LEAVE:
                p.leave();
                p.getMatch().write(new Message(Command.LEFT, new byte[] { player.getIdentifier() }));
                break;
            case PLAY:
                p.play(m.getArgument(0), m.getArgument(1), m.getArgument(2));
                p.getMatch().write(new Message(Command.PLAYED, new byte[] { p.getIdentifier(), m.getArgument(0), m.getArgument(1) }));
                break;
            default:
                // Unrecognised command
                return;
        }
    }

    public int write(Connection c, Message m) {
        return c.write(m.getBytes());
    }

    public void started(Match match) {
        for(Player player : match.getPlayers())
            players.put(player.getConnection(), player);
    }

    public void finished(Match match) {
        for(Player player : match.getPlayers())
            players.remove(player.getConnection());
    }

    public void leave(Player p) {
        players.remove(p.getConnection());
        if(p.getConnection().getSocketChannel().isConnected())
            join(c);
    }
}
