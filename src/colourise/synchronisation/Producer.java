package colourise.synchronisation;

public interface Producer<T> {
    void request(Consumer<T> sender);
}
