package colourise.state.match;

public final class Position {
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
