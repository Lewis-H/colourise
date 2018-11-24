package colourise.networking.protocol;

public enum Card {
    NONE,
    DOUBLE_MOVE,
    REPLACEMENT,
    FREEDOM;

    private static final Card[] values = Card.values();

    public static Card fromInt(int i) {
        return values[i];
    }
}
