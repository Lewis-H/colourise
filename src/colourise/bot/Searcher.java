package colourise.bot;

import colourise.state.match.Match;
import colourise.state.match.Player;
import colourise.state.match.Position;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class to search for valid moves.
 */
public class Searcher {
    /**
     * Searches for valid moves to be played with the "freedom" card.
     * @param match The match to search
     * @return A list of valid positions to play
     */
    public static List<Position> freedom(Match match) {
        List<Position> positions = new ArrayList<>();
        for(int row = 0; row < match.getRows(); row++)
            for(int column = 0; column < match.getColumns(); column++)
                if(!match.occupied(row, column))
                    positions.add(new Position(row, column));
        return positions;
    }

    /**
     * Searches for valid moves to be played with the "replacement" card.
     * @param match The match to search
     * @param me The player
     * @return A list of valid positions to play
     */
    public static List<Position> replacement(Match match, Player me) {
        List<Position> positions = new ArrayList<>();
        for(int row = 0; row < match.getRows(); row++)
            for(int column = 0; column < match.getColumns(); column++)
                if(match.occupied(row, column) && match.adjacent(row, column, me))
                    positions.add(new Position(row, column));
        return positions;
    }

    /**
     * Searches for valid moves to be played with no card.
     * @param match The match to search
     * @param me The player
     * @return A list of valid positions to play
     */
    public static List<Position> all(Match match, Player me) {
        List<Position> positions = new ArrayList<>();
        for(int row = 0; row < match.getRows(); row++)
            for(int column = 0; column < match.getColumns(); column++)
                if(!match.occupied(row, column) && match.adjacent(row, column, me))
                    positions.add(new Position(row, column));
        return positions;
    }
}
