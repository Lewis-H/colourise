package colourise.networking.protocol;

/**
 * Card enum
 */
public enum Card {
    NONE, // No card
    DOUBLE_MOVE, // Double move
    REPLACEMENT, // Replacement
    FREEDOM; // Freedom

    // Expensive operation, caching
    private static final Card[] values = Card.values();

    /**
     * Converts an integer into a card.
     * @param i Integer to convert
     * @return Converted card
     */
    public static Card fromInt(int i) {
        return values[i];
    }
}
