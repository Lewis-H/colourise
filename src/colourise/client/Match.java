package colourise.client;

import colourise.networking.protocol.Message;

import java.util.HashMap;
import java.util.Map;

public final class Match {
    private final Map<Integer, Player> players = new HashMap<>(5);
    private final int ROWS = 6,
                      COLUMNS = 10;
    private final Player[][] grid = new Player[ROWS][COLUMNS];
    private final MyPlayer me;
    private boolean left = false;
    private boolean finished = false;

    public MyPlayer getMe() {
        return me;
    }

    public Match(int id, int count) {
        players.put(id, me = new MyPlayer(id));
        for(int i = 0; i < count; i++)
            if(i != id)
                players.put(i, new Player(i));
    }

    public Player getOccupier(int row, int column) {
        if(row >= ROWS)
            throw new IllegalArgumentException("row");
        if(column >= COLUMNS)
            throw new IllegalArgumentException("column");
        return grid[row][column];
    }

    void update(Message m) throws MatchFinishedException, LeftMatchException {
        if(m == null)
            throw new IllegalArgumentException("m");
        if(left)
            throw new LeftMatchException(this);
        if(finished)
            throw new MatchFinishedException(this);
        switch(m.getCommand()) {
            case PLAYED:
                Player player = players.get(Byte.valueOf(m.getArgument(0)).intValue());
                int row = m.getArgument(1);
                int column = m.getArgument(2);
                grid[row][column] = player;
                System.out.println(m.getArgument(0) + " played (" + m.getArgument(1) + ", " + m.getArgument(2) + ").");
                break;
            case LEFT:
                players.remove(Byte.valueOf(m.getArgument(0)).intValue());
                break;
            case END:
                finished = true;
                throw new MatchFinishedException(this);
        }
    }

    public Message leave() throws LeftMatchException, MatchFinishedException {
        if(left)
            throw new LeftMatchException(this);
        if(finished)
            throw new MatchFinishedException(this);
        left = true;
        return Message.Factory.leave();
    }
}
