package colourise.server;

import java.util.*;

public class Match {
    private final int rows = 6, columns = 10;
    private Set<Player> players = new HashSet<>(5);
    private Player[][] grid = new Player[rows][columns];
    private Set<Player> blocked = new HashSet<>(5);
    private Map<Player, Set<Card>> cards = new HashMap<>();
    private Iterator<Player> iterator = players.iterator();
    private Player turn;

    public Match(Player[] players) {
        for(Player player : players) {
            this.players.add(player);
            cards.put(player, new HashSet<Card>(3));
            cards.get(player).add(Card.DoubleMove);
            cards.get(player).add(Card.Freedom);
            cards.get(player).add(Card.Replacement);
        }
    }

    public void play(int row, int column, Player player, Card card) {
        if(adjacent(row, column, player) && !occupied(row, column)) grid[row][column] = player;
        refresh();
    }

    private Player get(int row, int column) {
        return grid[row][column];
    }

    private void use(Player player, Card card) {
        cards.get(player).remove(card);
    }

    private boolean has(Player player, Card card) {
        return cards.get(player).contains(card);
    }

    private boolean occupied(int row, int column) {
        return get(row, column) != null;
    }

    private boolean blocked(int row, int column) {
        for(int r = row - 1; r <= row + 1; r++)
            for(int c = column - 1; c <= column + 1; c++)
                if(!occupied(r, c)) return false;
        return true;
    }

    private boolean adjacent(int row, int column, Player player) {
        for(int r = row - 1; r <= row + 1; r++)
            for(int c = column - 1; c <= column + 1; c++)
                if(inBounds(r, c) && get(r, c) == player) return true;
        return false;
    }

    private boolean inBounds(int row, int column) {
        return row >= 0 && column >= 0 && row < rows && column < columns;
    }

    private void refresh() {
        // Find the blocked players
        Set<Player> free = new HashSet<>(players.size() - blocked.size());
        for(int r = 0; r <= rows; r++) {
            for (int c = 0; c <= columns; c++) {
                Player player = get(r, c);
                if((has(player, Card.Freedom) || has(player, Card.Replacement)) && !free.contains(player) && !blocked.contains(player) && !blocked(r, c))
                    free.add(player);
            }
        }
        for(Player player : players) {
            if(!free.contains(player) && !blocked.contains(player))
                blocked.add(player);
        }
        // Find the next (free) player
        while(true) {
            if(!iterator.hasNext()) iterator = players.iterator();
            Player player = iterator.next();
            if(!blocked.contains(player)) {
                turn = player;
                break;
            }
        }
    }

}
