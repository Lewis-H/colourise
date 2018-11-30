package colourise.state.lobby;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Lobby<T> {
    private T leader = null;
    private final List<T> waiting = new ArrayList<>(5);
    private final int capacity;

    public T getLeader() {
        return leader;
    }

    public List<T> getWaiters() {
        return waiting;
    }

    public int size() {
        return waiting.size();
    }

    public int capacity() {
        return capacity;
    }

    public Lobby(int capacity) {
        this.capacity = capacity;
    }

    public void join(T obj) throws LobbyFullException {
        if(size() == capacity)
            throw new LobbyFullException(this);
        if(waiting.isEmpty())
            leader = obj;
        waiting.add(obj);
    }

    public void clear() {
        waiting.clear();
        leader = null;
    }

    public void leave(T obj) {
        waiting.remove(obj);
        if(obj == leader)
            leader = waiting.isEmpty() ? null : waiting.iterator().next();
    }
}
