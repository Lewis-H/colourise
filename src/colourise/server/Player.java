package colourise.server;

import colourise.networking.Connection;
import colourise.networking.protocol.Card;
import colourise.networking.protocol.Message;
import colourise.server.match.Match;
import colourise.server.match.MatchFinishedException;

import java.util.HashSet;
import java.util.Set;

public final class Player {
    private final Connection connection;
    private final Match match;
    private final Set<Card> cards = new HashSet<>(3);
    private final int identifier;

    public Connection getConnection() {
        return connection;
    }

    public Match getMatch() {
        return match;
    }

    public int getIdentifier() {
        return identifier;
    }

    public Player(Connection connection, Match match, int identifier) {
        this.connection = connection;
        this.match = match;
        this.identifier = identifier;
        cards.add(Card.DoubleMove);
        cards.add(Card.Freedom);
        cards.add(Card.Replacement);
    }

    public void play(int row, int column, Card card) throws MatchFinishedException {
        use(card);
        match.play(row, column, this, card);
    }

    private void use(Card card) {
        if(card != Card.None)
            if(!cards.remove(card))
                return; // Add exception
    }

    public boolean has(Card card) {
        return cards.contains(card);
    }

    public void leave() throws MatchFinishedException {
        match.leave(this);
    }
}
