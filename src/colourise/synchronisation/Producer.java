package colourise.synchronisation;

/**
 * Producer interface
 * @param <T> Message type
 */
public interface Producer<T> {
    /**
     * Queues a request on the producer.
     * @param sender The sending consumer.
     */
    void request(Consumer<T> sender);
}
