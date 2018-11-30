package colourise.networking.protocol;

/**
 * Command enum.
 */
public enum Command {
    // Command types
    HELLO,
    JOINED,
    LEAD,
    LEFT,
    START,
    BEGIN,
    PLAY,
    PLAYED,
    END,
    ERROR;

    // Expensive operation, cached here
    private static final Command[] values = Command.values();

    /**
     * Converts an integer into a command
     * @param i Integer to convert
     * @return Converted command
     */
    public static Command fromInt(int i) {
        return values[i];
    }

    /**
     * Converts an command into a byte
     * @return Command as a byte
     */
    public byte toByte() {
        return (byte) ordinal();
    }

    /**
     * Gets the argument length of a command.
     * @param command The command
     * @return The number of arguments
     */
    public static int getLength(Command command) {
        switch(command) {
            case HELLO:
                // 1 argument: spectating
                return 1;
            case JOINED:
                // 1 argument: count
                return 1;
            case LEAD:
                return 0;
            case LEFT:
                // 2 argument: identifier/count (match/lobby), next
                return 2;
            case START:
                return 0;
            case BEGIN:
                // 12 arguments: identifier, player count, all positions
                return 12;
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
