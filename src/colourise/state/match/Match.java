package colourise.state.match;

import colourise.networking.Connection;
import colourise.networking.protocol.Card;
import colourise.state.player.Player;

import java.util.*;

public class Match {
    public static final int MAX_PLAYERS = 5;
    private final int ROWS = 6,
                      COLUMNS = 10;
    private final List<Player> players = new ArrayList<>(MAX_PLAYERS);
    private final HashSet<Player> blocked = new HashSet<>(MAX_PLAYERS);
    private final Set<Start> starts = new HashSet<>(MAX_PLAYERS);
    private final Player[][] grid = new Player[ROWS][COLUMNS];
    private final Map<Player, Integer> scoreboard = new HashMap<>(MAX_PLAYERS);
    private int current = 0;
    private Player winner;
    private boolean finished = false;

    public Player getCurrent() {
        return players.get(current);
    }

    public Map<Player, Integer> getScoreboard() {
        return scoreboard;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public Set<Start> getStarts() {
        return starts;
    }

    public boolean isFinished() {
        return finished;
    }

    public Match(int count) {
        // This should always be the case, as enforced by the Lobby.
        assert count <= MAX_PLAYERS && count > 0;
        Random random = new Random();
        for(int i = 0; i < count; i++) {
            Player player = new Player(this, i);
            players.add(player);
            scoreboard.put(player, 0);
            int row = 0;
            int column = 0;
            do {
                row = random.nextInt(ROWS);
                column = random.nextInt(COLUMNS);
            } while(occupied(row, column));
            grid[row][column] = player;
            starts.add(new Start(player, row, column));
        }
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
                if(valid(r, c) && !occupied(r, c)) return false;
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

    private void refresh(boolean skip) throws MatchFinishedException {
        // Find the blocked players
        Set<Player> free = new HashSet<>(players.size() - blocked.size());
        for(int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLUMNS; c++) {
                Player player = get(r, c);
                if(player != null)
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
        Player current = getCurrent();
        players.remove(player);
        blocked.remove(player);
        if(players.size() == blocked.size()) {
            finish();
        } else if(getCurrent() == player) {
            next();
        } else {
            this.current = players.indexOf(current);
        }
    }

    private void finish() throws MatchFinishedException {
        finished = true;
        throw new MatchFinishedException(this);
    }
}
