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
    private Lobby lobby;
    private Server server;

    public Colourise(InetSocketAddress address) throws IOException {
        lobby = new Lobby(this);
        server = Binder.listen(address, this);
    }

    public void listen() throws IOException {
        server.listen();
    }

    @Override
    public void connected(Connection c) {
        lobby.join(c);
    }

    @Override
    public void disconnected(Connection c) {
        Player player = players.get(c);
        if(player != null) {
            // Player is in a game.
            player.leave();
            players.remove(player.getConnection());
        }else{
            // Player is in the lobby.
            lobby.leave(c);
        }
    }

    @Override
    public void read(Connection c) {
        byte[] bytes = c.read(c.getParser().getRemaining());
        if(c.getParser().getRemaining() == 0) {
            received(c, c.getParser().create());
            c.getParser().reset();
        }
    }

    private void received(Connection c, Message m) {
        if(players.containsKey(c)) {
            // Player is in a game.
        }else{
            // Player is in the lobby.
        }
    }

    public void started(Match match) {
        for(Player player : match.getPlayers())
            players.put(player.getConnection(), player);
    }

    public void finished(Match match) {
        for(Player player : match.getPlayers())
            players.remove(player.getConnection());
    }
}
