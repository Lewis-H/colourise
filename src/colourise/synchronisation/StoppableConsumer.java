package colourise.synchronisation;

public interface StoppableConsumer<T> {
    void push(Producer<T> sender, T obj);

    void stop(Producer<T> sender);
}
