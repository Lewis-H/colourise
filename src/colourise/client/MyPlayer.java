package colourise.client;

import colourise.networking.protocol.Card;
import colourise.networking.protocol.Message;
import colourise.state.player.Player;

import java.util.HashSet;
import java.util.Set;

public final class MyPlayer {
    private final Player internal;
    private final Set<Card> cards = new HashSet<>(3);

    public Player getInternal() {
        return internal;
    }

    public MyPlayer(Player internal) {
        this.internal = internal;
        cards.add(Card.FREEDOM);
        cards.add(Card.REPLACEMENT);
        cards.add(Card.DOUBLE_MOVE);
    }

    public Message play(int row, int column, Card card) {
        return Message.Factory.play(row, column, card);
    }
}
