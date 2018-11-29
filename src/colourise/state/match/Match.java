package colourise.state.match;

import colourise.networking.protocol.Card;
import colourise.state.player.Player;
import javafx.geometry.Pos;

import java.util.*;

public class Match {
    public static final int MAX_PLAYERS = 5;
    private final int ROWS = 6,
                      COLUMNS = 10;
    private final Map<Integer, Player> players = new HashMap<>(MAX_PLAYERS);
    private final Set<Player> blocked = new HashSet<>(MAX_PLAYERS);
    private final Map<Integer, Position> starts;
    private final Player[][] grid = new Player[ROWS][COLUMNS];
    private final Map<Player, Integer> scoreboard = new HashMap<>(MAX_PLAYERS);
    private int current = 0;
    private int filled = 0;
    private boolean finished = false;

    public boolean isFull() {
        return ROWS * COLUMNS == filled;
    }

    public Player getCurrent() {
        return players.get(current);
    }

    public Map<Player, Integer> getScoreboard() {
        return scoreboard;
    }

    public Collection<Player> getPlayers() {
        return players.values();
    }

    public Map<Integer, Position> getStarts() {
        return starts;
    }

    public boolean isFinished() {
        return finished;
    }

    public int getRows() {
        return ROWS;
    }

    public int getColumns() {
        return COLUMNS;
    }

    public Match(int count) {
        // This should always be the case, as enforced by the Lobby.
        assert count <= MAX_PLAYERS && count > 0;
        Random random = new Random();
        Map<Integer, Position> starts = new HashMap<>();
        for(int i = 0; i < count; i++) {
            int row;
            int column;
            do {
                row = random.nextInt(ROWS);
                column = random.nextInt(COLUMNS);
            } while(occupied(row, column));
            starts.put(i, new Position(row, column));
        }
        for(Map.Entry<Integer, Position> start : starts.entrySet()) {
            Player player = new Player(this, start.getKey());
            players.put(player.getIdentifier(), player);
            scoreboard.put(player, 0);
            grid[start.getValue().getRow()][start.getValue().getColumn()] = player;
        }
        this.starts = starts;
    }

    public Match(Map<Integer, Position> starts) {
        for(Map.Entry<Integer, Position> start : starts.entrySet()) {
            Player player = new Player(this, start.getKey());
            players.put(player.getIdentifier(), player);
            scoreboard.put(player, 0);
            grid[start.getValue().getRow()][start.getValue().getColumn()] = player;
        }
        this.starts = starts;
    }

    public void play(int row, int column, Player player, Card card) throws MatchFinishedException, NotPlayersTurnException, CannotPlayException, InvalidPositionException {
        if(player != getCurrent())
            throw new NotPlayersTurnException(this, player);
        if(isFinished())
            throw new MatchFinishedException(this);
        place(row, column, player, card);
        refresh(card == Card.DOUBLE_MOVE);
    }

    private void place(int row, int column, Player player, Card card) throws CannotPlayException, InvalidPositionException {
        if(!valid(row, column)) throw new InvalidPositionException(this, player, row, column);
        if((card == Card.FREEDOM || adjacent(row, column, player)) && (card == Card.REPLACEMENT || !occupied(row, column))) {
            // If the space is occupied (i.e. the replacement card has been used) then decrement the score of the player occupying the space
            if(occupied(row, column))
                decrement(get(row, column));
            else
                ++filled;
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

    public Player get(int row, int column) {
        return grid[row][column];
    }

    public boolean occupied(int row, int column) {
        return get(row, column) != null;
    }

    public boolean blocked(int row, int column) {
        for(int r = row - 1; r <= row + 1; r++)
            for(int c = column - 1; c <= column + 1; c++)
                if(valid(r, c) && !occupied(r, c)) return false;
        return true;
    }

    private boolean blocked(Player player) {
        return blocked.contains(player);
    }

    public boolean adjacent(int row, int column, Player player) {
        for(int r = row - 1; r <= row + 1; r++)
            for(int c = column - 1; c <= column + 1; c++)
                if(valid(r, c) && get(r, c) == player) return true;
        return false;
    }

    public boolean valid(int row, int column) {
        return row >= 0 && column >= 0 && row < ROWS && column < COLUMNS;
    }

    private void refresh(boolean skip) throws MatchFinishedException {
        // Find the blocked players
        Set<Player> free = new HashSet<>(players.size() - blocked.size());
        for(int row = 0; row < ROWS; row++) {
            for (int column = 0; column < COLUMNS; column++) {
                Player player = get(row, column);
                if(player != null && !free.contains(player) && !blocked(player))
                    if(((player.has(Card.FREEDOM) && !isFull()) || player.has(Card.REPLACEMENT)) || !blocked(row, column))
                        free.add(player);
            }
        }
        for(Player player : players.values()) {
            if(!free.contains(player) && !blocked(player))
                blocked.add(player);
        }
        // Set the next (free) player
        if(free.isEmpty())
            finish();
        // Skips selecting next player for double move, only if player is not blocked (to avoid halting the entire game).
        if(!skip || blocked.contains(getCurrent()))
            next();
    }

    private void next() throws MatchFinishedException {
        if(players.size() == blocked.size()) finish();
        for(int t = current + 1; t <= players.size() + current; t++) {
            int i = t % players.size();
            if(!blocked.contains(players.get(i))) {
                current = i;
                break;
            }
        }
    }

    public void leave(Player player) throws MatchFinishedException {
        players.remove(player);
        blocked.remove(player);
        Player c = getCurrent();
        if(players.size() == blocked.size() || players.size() == 0) {
            finish();
        } else if(getCurrent() == player) {
            next();
        }
    }

    private void finish() throws MatchFinishedException {
        finished = true;
        throw new MatchFinishedException(this);
    }
}
