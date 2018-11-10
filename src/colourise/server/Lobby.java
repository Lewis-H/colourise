package colourise.server;

import colourise.networking.Connection;

import java.util.HashSet;
import java.util.Set;

public class Lobby {
    private Connection leader = null;
    private Set<Connection> connections = new HashSet<>(5);
    private Colourise game;

    public Lobby(Colourise game) {
        this.game = game;
    }

    public void join(Connection connection) {
        if(connections.isEmpty())
            leader = connection;
        connections.add(connection);
        if(connections.size() == Match.MAX_PLAYERS)
            initialise();
    }

    private void initialise() {
        Match match = new Match(game, connections);
        connections.clear();
        leader = null;
        game.started(match);
    }

    public void leave(Connection connection) {
        connections.remove(connection);
        if(connection == leader)
            leader = connections.isEmpty() ? null : connections.iterator().next();
    }
}
