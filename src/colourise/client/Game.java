package colourise.client;

import colourise.ColouriseException;
import colourise.networking.protocol.Card;
import colourise.networking.protocol.Message;
import colourise.state.lobby.Lobby;
import colourise.state.match.CannotPlayException;
import colourise.state.match.InvalidPositionException;
import colourise.state.match.Match;
import colourise.state.match.NotPlayersTurnException;
import colourise.state.player.Player;
import colourise.state.match.MatchFinishedException;

import java.util.Iterator;

public class Game {
    private int lobby = 0;
    private boolean leader = false;
    private MyPlayer me = null;
    private Match match = null;
    private Stage stage = Stage.CONNECTED;

    public Match getMatch() {
        return match;
    }

    public Stage getStage() {
        return stage;
    }

    public boolean isLeader() {
        return leader;
    }

    public int size() {
        if(match != null)
            return match.getPlayers().size();
        else
            return lobby;
    }

    public void update(Message m) {
        switch (m.getCommand()) {
            case HELLO:
                leader = m.getArgument(0) != 0;
                lobby = m.getArgument(1);
                stage = Stage.LOBBY;
                break;
            case JOINED:
                lobby = m.getArgument(0);
            case LEFT:
                if (match == null)
                    lobby++;
                else {
                    Iterator<Player> it = match.getPlayers().iterator();
                    while(it.hasNext()) {
                        if(it.next().getIdentifier() == m.getArgument(0)) {
                            it.remove();
                            break;
                        }
                    }
                }
            case BEGIN:
                stage = Stage.MATCH;
                match = new Match(m.getArgument(1));
                Iterator<Player> it = match.getPlayers().iterator();
                while(it.hasNext()) {
                    Player player = it.next();
                    if(player.getIdentifier() == m.getArgument(0)) {
                        me = new MyPlayer(player);
                        break;
                    }
                }
                break;
            case PLAYED:
                try {
                    for (Player player : match.getPlayers())
                        if (player.getIdentifier() == m.getArgument(0))
                            match.play(m.getArgument(1), m.getArgument(2), player, Card.fromInt(m.getArgument(3)));
                }catch(MatchFinishedException ex) {
                } catch(NotPlayersTurnException | CannotPlayException | InvalidPositionException ex) {
                    ex.printStackTrace();
                    assert false; // Should never happen, crash if so
                }
                break;
            case END:
                break;
        }
    }
}
