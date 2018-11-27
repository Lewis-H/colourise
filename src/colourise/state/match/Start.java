package colourise.state.match;

import colourise.state.player.Player;

public final class Start {
    private final Player player;
    private final int row;
    private final int column;

    public Player getPlayer() {
        return player;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public Start(Player player, int row, int column) {
        this.player = player;
        this.row = row;
        this.column = column;
    }
}
