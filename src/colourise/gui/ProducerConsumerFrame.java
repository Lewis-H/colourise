package colourise.gui;

import colourise.networking.protocol.Message;
import colourise.synchronisation.Consumer;
import colourise.synchronisation.Producer;

import javax.swing.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public abstract class ProducerConsumerFrame<T> extends JFrame implements Producer<T>, Consumer<T> {
    private final BlockingQueue<Consumer<T>> requests = new LinkedBlockingQueue<>();

    protected Consumer<T> getRequest() throws InterruptedException {
        return requests.take();
    }

    protected Consumer<T> getRequest(long ms) throws InterruptedException {
        return requests.poll(ms, TimeUnit.MILLISECONDS);
    }

    @Override
    public void request(Consumer<T> sender) {
        requests.add(sender);
    }

    @Override
    public void push(Producer<T> sender, T message) {
        SwingUtilities.invokeLater(() -> consumed(sender, message));
    }

    protected abstract void consumed(Producer<T> sender, T obj);
}
