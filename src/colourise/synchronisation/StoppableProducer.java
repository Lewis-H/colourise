package colourise.synchronisation;

/**
 * Producer which can be stopped
 * @param <T> Message type
 */
public interface StoppableProducer<T> {
    void request(Consumer<T> sender);

    void stop(Consumer<T> sender);
}
