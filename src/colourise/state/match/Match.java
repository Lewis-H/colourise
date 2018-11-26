package colourise.state.match;

import colourise.networking.Connection;
import colourise.networking.protocol.Card;
import colourise.state.player.Player;

import java.util.*;

public class Match {
    public static final int MAX_PLAYERS = 5;
    private final int ROWS = 6,
                      COLUMNS = 10;
    private final Set<Player> players = new HashSet<>(MAX_PLAYERS),
                              blocked = new HashSet<>(5);
    private final Player[][] grid = new Player[ROWS][COLUMNS];
    private final Map<Player, Integer> scoreboard = new HashMap<>(MAX_PLAYERS);
    private Iterator<Player> iterator;
    private Player current;
    private Player winner;
    private boolean finished = false;

    public Player getCurrent() {
        return current;
    }

    public Map<Player, Integer> getScoreboard() {
        return scoreboard;
    }

    public Set<Player> getPlayers() {
        return players;
    }

    public boolean isFinished() {
        return finished;
    }

    public Match(int count) {
        // This should always be the case, as enforced by the Lobby.
        assert count <= MAX_PLAYERS;
        for(int i = 0; i < count; i++) {
            Player player = new Player(this, i);
            players.add(player);
            scoreboard.put(player, 0);
        }
        iterator = players.iterator();
        current = iterator.next();
    }

    public void play(int row, int column, Player player, Card card) throws MatchFinishedException, NotPlayersTurnException, CannotPlayException, InvalidPositionException {
        if(player != getCurrent())
            throw new NotPlayersTurnException(this, player);
        if(isFinished())
            throw new MatchFinishedException(this);
        place(row, column, player, card);
        refresh();
    }

    private void place(int row, int column, Player player, Card card) throws CannotPlayException, InvalidPositionException {
        if(!valid(row, column)) throw new InvalidPositionException(this, player, row, column);
        if((card == Card.FREEDOM || adjacent(row, column, player)) && (card == Card.REPLACEMENT || !occupied(row, column))) {
            // If the space is occupied (i.e. the replacement card has been used) then decrement the score of the player occupying the space
            if(occupied(row, column))
                decrement(get(row, column));
            // Place the player in the grid position
            grid[row][column] = player;
            // Increment the player's score
            increment(player);
        }else{
            throw new CannotPlayException(this, player, row, column);
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
        return row >= 0 && column >= 0 && row < ROWS && column < COLUMNS;
    }

    private void refresh() throws MatchFinishedException {
        // Find the blocked players
        Set<Player> free = new HashSet<>(players.size() - blocked.size());
        for(int r = 0; r <= ROWS; r++) {
            for (int c = 0; c <= COLUMNS; c++) {
                Player player = get(r, c);
                if((player.has(Card.FREEDOM) || player.has(Card.REPLACEMENT)) && !free.contains(player) && !blocked(player) && !blocked(r, c))
                    free.add(player);
            }
        }
        for(Player player : players) {
            if(!free.contains(player) && !blocked(player))
                blocked.add(player);
        }
        // Set the next (free) player
        if(free.isEmpty())
            finish();
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

    public void leave(Player player) throws MatchFinishedException {
        players.remove(player);
        blocked.remove(player);
        if(players.size() - blocked.size() == 0) {
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
    }

    private void finish() throws MatchFinishedException {
        finished = true;
        throw new MatchFinishedException(this);
    }
}
