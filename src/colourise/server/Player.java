package colourise.server;

import colourise.networking.Connection;
import colourise.state.match.Match;

public class Player extends colourise.state.player.Player {
    private final Connection connection;

    public Connection getConnection() {
        return connection;
    }

    public Player(Connection connection, Match match, int identifier) {
        super(match, identifier);
        this.connection = connection;
    }
}
