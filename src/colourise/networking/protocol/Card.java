package colourise.networking.protocol;

public enum Card {
    None,
    DoubleMove,
    Replacement,
    Freedom;

    private static final Card[] values = Card.values();

    public static Card fromInt(int i) {
        return values[i];
    }
}
