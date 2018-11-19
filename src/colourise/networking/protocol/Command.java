package colourise.networking.protocol;

public enum Command {
    HELLO,
    JOINED,
    LEAVE,
    LEFT,
    BEGIN,
    PLAY,
    PLAYED,
    END;

    // Expensive operation, cached here
    private static final Command[] values = Command.values();

    public static Command fromInt(int i) {
        return values[i];
    }

    public byte toByte() {
        return (byte) ordinal();
    }

    public static int getLength(Command command) {
        switch(command) {
            case HELLO:
                // 1 arguments: leader flag
                return 1;
            case JOINED:
                // 1 argument: count
                return 1;
            case LEAVE:
                // No arguments, player is leaving
                return 0;
            case LEFT:
                // 1 argument: identifier/count (match/lobby)
                return 1;
            case BEGIN:
                // 2 arguments: identifier, player count
                return 2;
            case PLAY:
                // 3 arguments: x, y, card
                return 3;
            case PLAYED:
                // 3 arguments: identifier, x, y
                return 3;
            case END:
                // 5 arguments: scores
                return 5;
            default:
                return 0;
        }
    }
}
