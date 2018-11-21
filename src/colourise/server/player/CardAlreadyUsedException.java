package colourise.server.player;

import colourise.networking.protocol.Card;

public final class CardAlreadyUsedException extends PlayerException {
    private final Card card;

    public Card getCard() {
        return card;
    }

    public CardAlreadyUsedException(Player p, Card c) {
        super(p);
        card = c;
    }

    public CardAlreadyUsedException(String message, Player p, Card c) {
        super(message, p);
        card = c;
    }

    public CardAlreadyUsedException(Throwable cause, Player p, Card c) {
        super(cause, p);
        card = c;
    }

    public CardAlreadyUsedException(String message, Throwable cause, Player p, Card c) {
        super(message, cause, p);
        card = c;
    }

    public CardAlreadyUsedException(String message, Throwable cause, boolean enableSuppression, boolean writableStacktrace, Player p, Card c) {
        super(message, cause, enableSuppression, writableStacktrace, p);
        card = c;
    }
}
