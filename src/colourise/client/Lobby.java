package colourise.client;

import colourise.networking.protocol.Message;

public class Lobby {
    private int players;
    private final boolean leader;

    public int getPlayers() {
        return players;
    }

    public boolean isLeader() {
        return leader;
    }

    public Lobby(boolean leader, int players) {
        this.leader = leader;
        this.players = players;
    }

    void update(Message m) throws MatchBegunException {
        switch(m.getCommand()) {
            case JOINED:
            case LEFT:
                players = m.getArgument(0);
                break;
            case BEGIN:
                throw new MatchBegunException(new Match(m.getArgument(0), m.getArgument(1)));
        }
    }
}
