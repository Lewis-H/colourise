package colourise.synchronisation;

/**
 * Interface for consumers which can be stopped.
 * @param <T> Message type
 */
public interface StoppableConsumer<T> {
    void push(Producer<T> sender, T obj);

    void stop(Producer<T> sender);
}
