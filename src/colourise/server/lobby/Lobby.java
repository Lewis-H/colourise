package colourise.server.lobby;

import colourise.networking.Connection;
import colourise.networking.protocol.Message;
import colourise.server.Colourise;
import colourise.server.match.Match;

import java.util.HashSet;
import java.util.Set;

public class Lobby {
    private Connection leader = null;
    private final Set<Connection> connections = new HashSet<>(5);

    public Connection getLeader() {
        return leader;
    }

    public int count() {
        return connections.size();
    }

    public Lobby() { }

    public void join(Connection connection) throws MatchStartedException {
        if(connections.isEmpty())
            leader = connection;
        connections.add(connection);
        if(connections.size() == Match.MAX_PLAYERS)
            initialise();
    }

    private void initialise() throws MatchStartedException{
        Match match = new Match(connections);
        connections.clear();
        leader = null;
        throw new MatchStartedException(this, match);
    }

    public void leave(Connection connection) {
        connections.remove(connection);
        if(connection == leader)
            leader = connections.isEmpty() ? null : connections.iterator().next();
    }

    public void write(Message m) {
        byte[] bytes = m.toBytes();
        for(Connection c : connections)
            c.write(bytes);
    }
}
