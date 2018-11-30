package colourise.synchronisation;

/**
 * Consumer interface
 * @param <T> Message type
 */
public interface Consumer<T> {
    /**
     * Pushes a message to the consumer to be processed.
     * @param sender Sending producer
     * @param obj Message
     */
    void push(Producer<T> sender, T obj);
}
