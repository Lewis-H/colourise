package colourise.state.match;

import colourise.networking.protocol.Card;

import java.util.HashSet;
import java.util.Set;

public class Player {
    private final Match match;
    private final Set<Card> cards = new HashSet<>(3);
    private final int identifier;

    public Match getMatch() {
        return match;
    }

    public int getIdentifier() {
        return identifier;
    }

    public Player(Match match, int identifier) {
        this.match = match;
        this.identifier = identifier;
        cards.add(Card.DOUBLE_MOVE);
        cards.add(Card.FREEDOM);
        cards.add(Card.REPLACEMENT);
    }

    public void play(int row, int column, Card card) throws MatchFinishedException, NotPlayersTurnException, InvalidPositionException, CannotPlayException, CardAlreadyUsedException {
        match.play(row, column, this, card);
    }

    void use(Card card) throws CardAlreadyUsedException {
        if(card != Card.NONE)
            if(!cards.remove(card))
                throw new CardAlreadyUsedException(this, card);
    }

    public boolean has(Card card) {
        return cards.contains(card);
    }

    public void leave() throws MatchFinishedException {
        match.leave(this);
    }
}
