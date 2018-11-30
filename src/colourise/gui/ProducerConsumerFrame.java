package colourise.gui;

import colourise.synchronisation.Consumer;
import colourise.synchronisation.Producer;

import javax.swing.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * JFrame which is a Producer and Consumer
 * @param <T> Message type
 */
public abstract class ProducerConsumerFrame<T> extends JFrame implements Producer<T>, Consumer<T> {
    // Request queue
    private final BlockingQueue<Consumer<T>> requests = new LinkedBlockingQueue<>();

    /**
     * Gets the next request.
     * NOTE: Blocks if no request is available!
     * @return Request
     * @throws InterruptedException
     */
    protected Consumer<T> getRequest() throws InterruptedException {
        return requests.take();
    }

    /**
     * Gets the next request with a timeout.
     * @param ms
     * @return
     * @throws InterruptedException
     */
    protected Consumer<T> getRequest(long ms) throws InterruptedException {
        return requests.poll(ms, TimeUnit.MILLISECONDS);
    }

    /**
     * Post a consumer request.
     * @param sender Request sender
     */
    @Override
    public void request(Consumer<T> sender) {
        requests.add(sender);
    }

    /**
     * Push a message.
     * @param sender Sender of the message
     * @param message Message
     */
    @Override
    public void push(Producer<T> sender, T message) {
        SwingUtilities.invokeLater(() -> consumed(sender, message));
    }

    /**
     * Message consumer.
     * @param sender Sender of the message
     * @param obj Message
     */
    protected abstract void consumed(Producer<T> sender, T obj);
}
