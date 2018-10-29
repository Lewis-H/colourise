package colourise.server;

import java.util.HashSet;

public class Match {
    private final int rows = 6, columns = 10;
    private HashSet<Player> players = new HashSet<>(5);
    private Player[][] grid = new Player[rows][columns];

    public play(int row, int column, Player player) {
        if(adjacent(row, column, player) && !occupied(row, column)) grid[row][column] = player;
    }

    private boolean occupied(int row, int column) {
        return grid[row][column] != null;
    }

    private boolean adjacent(int row, int column, Player player) {
        for(int r = row - 1; r <= row + 1; r++)
            for(int c = column - 1; c <= column + 1; c++)
                if(inBounds(r, c) && grid[r][c] == player) return true;
        return false;
    }

    private boolean inBounds(int row, int column) {
        return row >= 0 && column >= 0 && row < rows && column < columns;
    }

}
