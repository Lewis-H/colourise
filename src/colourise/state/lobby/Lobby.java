package colourise.state.lobby;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Lobby state
 * @param <T> Lobbied object
 */
public class Lobby<T> {
    // Leader
    private T leader = null;
    // Waiting in the lobby
    private final List<T> waiting = new ArrayList<>(5);
    // Capacity of lobby
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
        // Add object to lobby
        if(size() == capacity)
            throw new LobbyFullException(this);
        if(waiting.isEmpty())
            leader = obj;
        waiting.add(obj);
    }

    public void clear() {
        // Clear lobby
        waiting.clear();
        leader = null;
    }

    public boolean leave(T obj) {
        // Remove object from lobby
        boolean left = waiting.remove(obj);
        if(obj == leader)
            leader = waiting.isEmpty() ? null : waiting.iterator().next();
        return left;
    }
}
