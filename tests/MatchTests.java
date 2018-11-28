import colourise.ColouriseException;
import colourise.networking.protocol.Card;
import colourise.state.match.Match;
import colourise.state.match.Start;
import colourise.state.player.Player;
import org.junit.Test;

public class MatchTests {
    @Test
    public void testMakeMove() {
        Match match = new Match(1);
        Player player = match.getPlayers().iterator().next();
        Start start = match.getStarts().iterator().next();
        int row, column;
        if(start.getRow() - 1 < 0 || start.getColumn() - 1 < 0) {
            row = start.getRow() + 1;
            column = start.getColumn() + 1;
        }else{
            row = start.getRow() - 1;
            column = start.getColumn() - 1;
        }
        System.out.println(row);
        System.out.println(column);
        try {
            match.play(row, column, player, Card.NONE);
            assert(true);
        } catch(ColouriseException ex) {
            assert(false);
        }
    }
}
