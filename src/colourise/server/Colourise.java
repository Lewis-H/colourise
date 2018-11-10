package colourise.server;

import colourise.networking.Connection;
import colourise.networking.Listener;
import colourise.networking.Server;

import java.util.HashMap;
import java.util.Map;

public class Colourise implements Listener {
    private final Map<Connection, Player> players = new HashMap<>();
    private Lobby lobby;
    private Server server;

    public Colourise(Server server) {
        this.server = server;
        lobby = new Lobby(this);
    }

    @Override
    public void connected(Connection c) {
        lobby.join(c);
    }

    @Override
    public void disconnected(Connection c) {
        if(players.containsKey(c)) {
            // Player is in a game.
        }else{
            // Player is in the lobby.
            lobby.leave(c);
        }
    }

    @Override
    public void read(Connection c) {
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
}
