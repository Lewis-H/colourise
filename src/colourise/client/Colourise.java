package colourise.client;

import colourise.networking.protocol.Message;

public class Colourise {
    private Match match = null;
    private Lobby lobby = null;

    public Match getMatch() {
        return match;
    }

    public Lobby getLobby() {
        return lobby;
    }

    private void updateLobby(Message m) throws MatchBegunException {
        try {
            switch (m.getCommand()) {
                case JOINED:
                case LEFT:
                case BEGIN:
                    lobby.update(m);
                    break;
            }
        }catch(MatchBegunException ex) {
            match = ex.getMatch();
            throw ex;
        }
    }

    private void updateMatch(Message m) throws MatchFinishedException, LeftMatchException {
        try {
            switch (m.getCommand()) {
                case JOINED:
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
        if (lobby == null & match == null) {
            switch (m.getCommand()) {
                case HELLO:
                    lobby = new Lobby(m.getArgument(0) != 0, m.getArgument(1));
                    break;
            }
        } else if (lobby != null) {
            updateLobby(m);
        } else if (match != null) {
            updateMatch(m);
        }
    }
}
