package colourise.state.match;

import colourise.networking.protocol.Card;

import java.util.*;

public class Match {
    public static final int MAX_PLAYERS = 5;
    private static final int ROWS = 6,
                      COLUMNS = 10;
    private final Map<Integer, Player> players = new HashMap<>(MAX_PLAYERS);
    private final Set<Player> blocked = new HashSet<>(MAX_PLAYERS);
    private final Map<Integer, Position> starts;
    private final Player[][] grid = new Player[getRows()][getColumns()];
    private final Map<Player, Integer> scoreboard = new HashMap<>(MAX_PLAYERS);
    private int current = 0;
    private int filled = 0;
    private boolean finished = false;

    public boolean isFull() {
        return getRows() * getColumns() == filled;
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
                row = random.nextInt(getRows());
                column = random.nextInt(getColumns());
            } while(occupied(row, column));
            starts.put(i, new Position(row, column));
        }
        for(Map.Entry<Integer, Position> start : starts.entrySet()) {
            Player player = new Player(this, start.getKey());
            players.put(player.getIdentifier(), player);
            scoreboard.put(player, 1);
            grid[start.getValue().getRow()][start.getValue().getColumn()] = player;
            ++filled;
        }
        this.starts = starts;
    }

    public Match(Map<Integer, Position> starts) {
        for(Map.Entry<Integer, Position> start : starts.entrySet()) {
            Player player = new Player(this, start.getKey());
            players.put(player.getIdentifier(), player);
            scoreboard.put(player, 1);
            grid[start.getValue().getRow()][start.getValue().getColumn()] = player;
            ++filled;
        }
        this.starts = starts;
    }

    public void play(int row, int column, Player player, Card card) throws MatchFinishedException, NotPlayersTurnException, CannotPlayException, InvalidPositionException, CardAlreadyUsedException {
        if(player != getCurrent())
            throw new NotPlayersTurnException(this, player);
        if(isFinished())
            throw new MatchFinishedException(this);
        place(row, column, player, card);
        refresh(card == Card.DOUBLE_MOVE);
    }

    private void place(int row, int column, Player player, Card card) throws CannotPlayException, InvalidPositionException, CardAlreadyUsedException {
        if(!valid(row, column))
            throw new InvalidPositionException(this, player, row, column);
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
            // Mark card as used
            player.use(card);
        }else{
            throw new CannotPlayException(this, player, row, column);
        }
    }

    /**
     * Increments the specified players score
     * @param player
     */
    private void increment(Player player) {
        scoreboard.put(player, scoreboard.get(player) + 1);
    }

    /**
     * Decrements the specified players score
     * @param player
     */
    private void decrement(Player player) {
        scoreboard.put(player, scoreboard.get(player) - 1);
    }

    /**
     * Gets the specified player occupying a position
     * @param row
     * @param column
     * @return
     */
    public Player get(int row, int column) {
        return grid[row][column];
    }

    /**
     * Gets whether the specified position is occupied
     * @param row
     * @param column
     * @return
     */
    public boolean occupied(int row, int column) {
        return get(row, column) != null;
    }

    /**
     * Gets whether the specified position is blocked
     * @param row
     * @param column
     * @return
     */
    public boolean blocked(int row, int column) {
        for(int r = row - 1; r <= row + 1; r++)
            for(int c = column - 1; c <= column + 1; c++)
                if(valid(r, c) && !occupied(r, c)) return false;
        return true;
    }

    /**
     * Gets whether the specified player is blocked
     * @param player
     * @return
     */
    public boolean blocked(Player player) {
        return blocked.contains(player);
    }

    /**
     * Gets whether the specified player has a position adjacent to the specified position
     * @param row
     * @param column
     * @param player
     * @return
     */
    public boolean adjacent(int row, int column, Player player) {
        for(int r = row - 1; r <= row + 1; r++)
            for(int c = column - 1; c <= column + 1; c++)
                if(!(r == row && c == column))
                    if(valid(r, c) && get(r, c) == player)
                        return true;
        return false;
    }

    /**
     * Gets whether the specified position is valid
     * @param row
     * @param column
     * @return
     */
    public boolean valid(int row, int column) {
        return row >= 0 && column >= 0 && row < getRows() && column < getColumns();
    }

    /**
     * Refreshes the match after each play
     * @param skip
     * @throws MatchFinishedException
     */
    private void refresh(boolean skip) throws MatchFinishedException {
        // Find the blocked players
        Set<Player> free = new HashSet<>(players.size() - blocked.size());
        for(int row = 0; row < getRows(); row++) {
            for(int column = 0; column < getColumns(); column++) {
                Player player = get(row, column);
                if(player != null && !free.contains(player) && !blocked(player))
                    if(((player.has(Card.FREEDOM) && !isFull()) || player.has(Card.REPLACEMENT)) || !blocked(row, column))
                        free.add(player);
            }
        }
        for(Player player : players.values())
            if(!free.contains(player) && !blocked(player))
                blocked.add(player);
        // Set the next (free) player
        if(free.isEmpty())
            finish();
        // Skips selecting next player for double move, only if player is not blocked (to avoid halting the entire game).
        if(!skip || blocked.contains(getCurrent()))
            next();
    }

    /**
     * Selects the player whose turn it is next
     * @throws MatchFinishedException
     */
    private void next() throws MatchFinishedException {
        if(players.size() == blocked.size()) finish();
        for(int t = current + 1; t <= MAX_PLAYERS + current; t++) {
            int i = t % MAX_PLAYERS;
            if(players.containsKey(i)) {
                if (!blocked.contains(players.get(i))) {
                    current = i;
                    break;
                }
            }
        }
    }

    /**
     * Removes the specified player from the match
     * @param player
     * @throws MatchFinishedException
     */
    public void leave(Player player) throws MatchFinishedException {
        players.remove(player.getIdentifier());
        blocked.remove(player);
        if(players.size() == blocked.size()) {
            finish();
        } else if(current == player.getIdentifier()) {
            next();
        }
    }

    /**
     * Finishes the match
     * @throws MatchFinishedException
     */
    private void finish() throws MatchFinishedException {
        finished = true;
        throw new MatchFinishedException(this);
    }
}
