package colourise.client;

import colourise.networking.protocol.Message;
import colourise.state.lobby.Lobby;
import colourise.state.match.Match;
import colourise.state.player.Player;

import java.util.Iterator;

import static colourise.networking.protocol.Command.BEGIN;

public class Client {
    private int lobby = 0;
    private boolean leader = false;
    private MyPlayer me = null;
    private Match match = null;

    public Match getMatch() {
        return match;
    }

    public Lobby getLobby() {
        return lobby;
    }

    private void updateMatch(Message m) throws MatchFinishedException, LeftMatchException {
        try {
            switch (m.getCommand()) {
                case LEFT:
                case PLAYED:
                case END:
                    match.update(m);
                    break;
            }
        } catch (MatchFinishedException | LeftMatchException ex) {
            match = null;
            throw ex;
        }
    }

    public void update(Message m) throws MatchFinishedException, LeftMatchException, MatchBegunException {
        switch (m.getCommand()) {
            case HELLO:
                leader = m.getArgument(0) != 0;
                lobby = m.getArgument(1);
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
                Iterator<Player> it = match.getPlayers().iterator();
                while(it.hasNext()) {
                    Player player = it.next();
                    if(player).getIdentifier() == m.getArgument(0))
                        match.play(m.getArgument(1), m.getArgument(2), player);
                }

        }
    }
}
