import colourise.ColouriseException;
import colourise.networking.protocol.Card;
import colourise.state.match.*;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.fail;

public class MatchTests {
    private Match setupSinglePlayerMatch() {
        return setupSinglePlayerMatch(0, 0);
    }

    private Match setupSinglePlayerMatch(int row, int column) {
        Map<Integer, Position> starts = new HashMap<Integer, Position>();
        starts.put(0, new Position(row, column));
        return new Match(starts);
    }

    private Match setupTwoPlayerMatch() {
        Map<Integer, Position> starts = new HashMap<Integer, Position>();
        starts.put(0, new Position(0, 0));
        starts.put(1, new Position(1, 0));
        return new Match(starts);
    }

    @Test
    public void moveIsPlaced() {
        Match match = setupSinglePlayerMatch();
        Player player = match.getPlayers().iterator().next();
        try {
            player.play(0, 1, Card.NONE);
            assertEquals(match.get(0, 1), player);
        }catch(ColouriseException ex) {
            fail("Exception thrown: " + ex.toString());
        }
    }

    @Test
    public void freedomMoveIsPlaced() {
        Match match = setupSinglePlayerMatch();
        Player player = match.getPlayers().iterator().next();
        try {
            player.play(5, 9, Card.FREEDOM);
            assertEquals(match.get(5, 9), player);
        }catch(ColouriseException ex) {
            fail("Exception thrown: " + ex.toString());
        }
    }

    @Test
    public void filledMatchFinishesOnlyOnceReplacementCardIsUsed() {
        Match match = setupSinglePlayerMatch();
        Player player = match.getPlayers().iterator().next();
        try {
            for(int row = 0; row < match.getRows(); row++)
                for(int column = 0; column < match.getColumns(); column++)
                    if(!match.occupied(row, column))
                        player.play(row, column, Card.NONE);
        }catch(ColouriseException ex) {
            fail("Exception thrown: " + ex.toString());
        }
        try {
            player.play(0, 0, Card.REPLACEMENT);
        } catch(MatchFinishedException ex) {
            return; // passed
        } catch(ColouriseException ex) {
            fail("Exception thrown: " + ex.toString());
        }
        fail("Match never ended.");
    }

    @Test
    public void replacementOfOnlyPositionBlocksPlayer() {
        Match match = setupTwoPlayerMatch();
        Player[] players = new Player[2];
        for(Player player : match.getPlayers())
            players[player.getIdentifier()] = player;
        try {
            players[0].play(1, 0, Card.REPLACEMENT);
        } catch(ColouriseException ex) {
            fail("Exception thrown: " + ex.toString());
        }
        assertTrue(match.blocked(players[1]));
    }

    @Test
    public void extremePositionsArePlayable() {
        Match match = setupSinglePlayerMatch(1, 1);
        Player player = match.getPlayers().iterator().next();
        try {
            player.play(0, 0, Card.FREEDOM);
        } catch(ColouriseException ex) {
            fail("Exception thrown: " + ex.toString());
        }
        match = setupSinglePlayerMatch(1, 1);
        player = match.getPlayers().iterator().next();
        try {
            player.play(0, match.getColumns() - 1, Card.FREEDOM);
        } catch(ColouriseException ex) {
            fail("Exception thrown: " + ex.toString());
        }
        match = setupSinglePlayerMatch(1, 1);
        player = match.getPlayers().iterator().next();
        try {
            player.play(match.getRows() - 1, 0, Card.FREEDOM);
        } catch(ColouriseException ex) {
            fail("Exception thrown: " + ex.toString());
        }
        match = setupSinglePlayerMatch(1, 1);
        player = match.getPlayers().iterator().next();
        try {
            player.play(match.getRows() - 1, match.getColumns() - 1, Card.FREEDOM);
        } catch(ColouriseException ex) {
            fail("Exception thrown: " + ex.toString());
        }
    }

    @Test
    public void invalidPositionsArentPlayable() {
        Match match = setupSinglePlayerMatch();
        Player player = match.getPlayers().iterator().next();
        try {
            player.play(-1, -1, Card.FREEDOM);
            fail("No exception thrown");
        } catch(InvalidPositionException ex) {
        } catch(ColouriseException ex) {
            fail("Exception thrown: " + ex.toString());
        }
        match = setupSinglePlayerMatch();
        player = match.getPlayers().iterator().next();
        try {
            player.play(0, match.getColumns(), Card.FREEDOM);
            fail("No exception thrown");
        } catch(InvalidPositionException ex) {
        } catch(ColouriseException ex) {
            fail("Exception thrown: " + ex.toString());
        }
        match = setupSinglePlayerMatch();
        player = match.getPlayers().iterator().next();
        try {
            player.play(match.getRows(), 0, Card.FREEDOM);
            fail("No exception thrown");
        } catch(InvalidPositionException ex) {
        } catch(ColouriseException ex) {
            fail("Exception thrown: " + ex.toString());
        }
        match = setupSinglePlayerMatch();
        player = match.getPlayers().iterator().next();
        try {
            player.play(match.getRows(), match.getColumns(), Card.FREEDOM);
            fail("No exception thrown");
        } catch(InvalidPositionException ex) {
        } catch(ColouriseException ex) {
            fail("Exception thrown: " + ex.toString());
        }
    }

    @Test
    public void freedomCardPreventsBlocking() {
        Match match = setupTwoPlayerMatch();
        Player[] players = new Player[2];
        for(Player player : match.getPlayers())
            players[player.getIdentifier()] = player;
        try {
            for(int column = 1; column < match.getColumns(); column++)
                for(int row = 0; row < 2; row++)
                    players[row].play(row, column, Card.NONE);
        } catch(ColouriseException ex) {
            fail("Exception thrown: " + ex.toString());
        }
        assertFalse(match.blocked(players[0]));
        try {
            players[0].play(2, 0, Card.FREEDOM);
        } catch(ColouriseException ex) {
            fail("Exception thrown: " + ex.toString());
        }
        assertEquals(match.get(2, 0), players[0]);
    }

    @Test
    public void cannotReplacePosition() {
        Match match = setupTwoPlayerMatch();
        Player[] players = new Player[2];
        for(Player player : match.getPlayers())
            players[player.getIdentifier()] = player;
        try {
            players[0].play(1, 0, Card.NONE);
        } catch(CannotPlayException ex) {
            return; // success
        } catch(ColouriseException ex) {
            fail("Exception thrown: " + ex.toString());
        }
    }
}
