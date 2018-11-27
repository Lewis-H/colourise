package colourise.synchronisation;

public interface StoppableProducer<T> {
    void request(Consumer<T> sender);

    void stop(Consumer<T> sender);
}
