package colourise.synchronisation;

public interface Consumer<T> {
    void push(Producer<T> sender, T obj);
}
