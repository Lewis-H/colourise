package colourise.networking.protocol;

public enum Command {
    JOIN,
    LEAVE,
    BEGIN,
    PLACE,
    PLACED,
    END;

    // Expensive operation, cached here
    private static final Command[] values = Command.values();

    public static Command fromInt(int i) {
        return values[i];
    }

    public static int getLength(Command command) {
        switch(command) {
            case JOIN:
                // 4 arguments: identifier, red, blue, green
                return 4;
            case LEAVE:
                // 1 argument: identifier
                return 1;
            case BEGIN:
                // No arguments, marks beginning of a game
                return 0;
            case PLACE:
                // 2 arguments: x, y
                return 2;
            case PLACED:
                // 3 arguments: identifier, x, y
                return 3;
            case END:
                // No arguments, marks end of a game
                return 0;
            default:
                return 0;
        }
    }
}
