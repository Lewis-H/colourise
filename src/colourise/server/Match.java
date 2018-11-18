package colourise.server;

import colourise.networking.Connection;

import java.util.*;

public class Match {
    public static final int MAX_PLAYERS = 5;
    private final int rows = 6,
                      columns = 10;
    private final Set<Player> players = new HashSet<>(MAX_PLAYERS),
                              blocked = new HashSet<>(5);
    private final Player[][] grid = new Player[rows][columns];
    private final Map<Player, Integer> scoreboard = new HashMap<>(MAX_PLAYERS);
    private Colourise game;
    private Iterator<Player> iterator;
    private Player current;

    public Player getCurrent() {
        return current;
    }

    public Map<Player, Integer> getScoreboard() {
        return new HashMap<>(scoreboard);
    }

    public Set<Player> getPlayers() {
        return new HashSet<>(players);
    }

    public Colourise getGame() {
        return game;
    }

    public Match(Colourise game, Collection<Connection> connections) {
        // This should always be the case, as enforced by the Lobby.
        assert connections.size() <= MAX_PLAYERS;
        this.game = game;
        for(Connection connection : connections) {
            Player player = new Player(connection, this, 0);
            players.add(player);
            scoreboard.put(player, 0);
        }
        iterator = players.iterator();
        current = iterator.next();
    }

    public void play(int row, int column, Player player, Card card) {
        if(player != getCurrent())
            return; // Add exception
        place(row, column, player, card);
        refresh();
    }

    private void place(int row, int column, Player player, Card card) {
        if(!valid(row, column)) return; // Add exception
        if((card == Card.Freedom || adjacent(row, column, player)) && (card == Card.Replacement || !occupied(row, column))) {
            // If the space is occupied (i.e. the replacement card has been used) then decrement the score of the player occupying the space
            if(occupied(row, column))
                decrement(get(row, column));
            // Place the player in the grid position
            grid[row][column] = player;
            // Increment the player's score
            increment(player);
        }else{
            return; // Add exception
        }
    }

    private void increment(Player player) {
        scoreboard.put(player, scoreboard.get(player) + 1);
    }

    private void decrement(Player player) {
        scoreboard.put(player, scoreboard.get(player) - 1);
    }

    private Player get(int row, int column) {
        return grid[row][column];
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

    private boolean blocked(Player player) {
        return blocked.contains(player);
    }

    private boolean adjacent(int row, int column, Player player) {
        for(int r = row - 1; r <= row + 1; r++)
            for(int c = column - 1; c <= column + 1; c++)
                if(valid(r, c) && get(r, c) == player) return true;
        return false;
    }

    private boolean valid(int row, int column) {
        return row >= 0 && column >= 0 && row < rows && column < columns;
    }

    private void refresh() {
        // Find the blocked players
        Set<Player> free = new HashSet<>(players.size() - blocked.size());
        for(int r = 0; r <= rows; r++) {
            for (int c = 0; c <= columns; c++) {
                Player player = get(r, c);
                if((player.has(Card.Freedom) || player.has(Card.Replacement)) && !free.contains(player) && !blocked(player) && !blocked(r, c))
                    free.add(player);
            }
        }
        for(Player player : players) {
            if(!free.contains(player) && !blocked(player))
                blocked.add(player);
        }
        // Set the next (free) player
        if(free.isEmpty())
            return; // Add exception
        next();
    }

    private void next() {
        while(true) {
            if(!iterator.hasNext()) iterator = players.iterator();
            Player player = iterator.next();
            if(!blocked(player)) {
                current = player;
                break;
            }
        }
    }

    public void leave(Player player) {
        players.remove(player);
        blocked.remove(player);
        if(players.size() == 0) {
            finish();
        } else {
            if(current == player) {
                iterator.remove();
                next();
            } else {
                iterator = players.iterator();
                Player c = current;
                do {
                    this.current = iterator.next();
                } while (current != c);
            }
        }
        game.leave(player);
    }

    public int write(Message m) {
        byte[] bytes = m.getBytes();
        for(Player p : players)
            p.getConnection().write(bytes);
    }

    private void finish() {
        game.finished(this);
    }
}
