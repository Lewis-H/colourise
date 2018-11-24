package colourise.client;

import colourise.networking.protocol.Card;
import colourise.networking.protocol.Message;

import java.util.HashSet;
import java.util.Set;

public class MyPlayer extends Player {
    private final Set<Card> cards = new HashSet<>(3);

    public MyPlayer(int id) {
        super(id);
        cards.add(Card.FREEDOM);
        cards.add(Card.REPLACEMENT);
        cards.add(Card.DOUBLE_MOVE);
    }

    public Message play(int row, int column, Card card) {
        return Message.Factory.play(row, column, card);
    }
}
