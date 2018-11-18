package colourise.server;

import colourise.networking.Connection;

import java.util.HashSet;
import java.util.Set;

public final class Player {
    private final Connection connection;
    private final Match match;
    private final Set<Card> cards = new HashSet<>(3);
    private final int identifier;
    private final Colourise game;

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
        this.game = match.getGame();
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

    public int write(Message m) {
        game.write(this, m);
    }

    public void leave() {
        match.leave(this);
    }
}
