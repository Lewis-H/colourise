package colourise.networking.protocol;

public enum Error {
    CANNOT_PLAY,
    INVALID_POSITION,
    NOT_PLAYERS_TURN,
    CARD_ALREADY_USED;

    private static final Error[] values = Error.values();

    public static Error fromInt(int i) {
        return values[i];
    }

    public byte toByte() {
        return (byte) ordinal();
    }
}
