package colourise.state.match;

/**
 * Represents a position
 */
public final class Position {
    // Row and column
    private final int row;
    private final int column;

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public Position(int row, int column) {
        this.row = row;
        this.column = column;
    }
}
