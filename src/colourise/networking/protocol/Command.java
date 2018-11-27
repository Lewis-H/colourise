package colourise.networking.protocol;

public enum Command {
    HELLO,
    JOINED,
    LEAVE,
    LEFT,
    START,
    BEGIN,
    PLAY,
    PLAYED,
    END,
    ERROR;

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
                // 1 arguments: leader flag, count
                return 2;
            case JOINED:
                // 1 argument: count
                return 1;
            case LEAVE:
                // No arguments, player is leaving
                return 0;
            case LEFT:
                // 2 argument: identifier/count (match/lobby), next
                return 2;
            case START:
                return 0;
            case BEGIN:
                // 2 arguments: identifier, player count
                return 2;
            case PLAY:
                // 3 arguments: row, column, card
                return 3;
            case PLAYED:
                // 5 arguments: identifier, row, column, card, next
                return 5;
            case END:
                // 5 arguments: scores
                return 5;
            case ERROR:
                // 1 argument: error
                return 1;
            default:
                return 0;
        }
    }
}
