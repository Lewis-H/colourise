package colourise.server;

import colourise.networking.Connection;

import java.util.HashSet;
import java.util.Set;

public class Player {
    private final Connection connection;
    private final Match match;
    private final int colour;
    private Set<Card> cards = new HashSet<>(3);

    public Connection getConnection() {
        return connection;
    }

    public Player(Connection connection, Match match, int colour) {
        this.connection = connection;
        this.match = match;
        this.colour = colour;
        cards.add(Card.DoubleMove);
        cards.add(Card.Freedom);
        cards.add(Card.Replacement);
    }

    public void play(int row, int column, Card card) {
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

    public void leave() {
        match.leave(this);
    }
}
