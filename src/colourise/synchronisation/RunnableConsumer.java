package colourise.synchronisation;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Consumer which runs on a separate thread with Runnable.
 * @param <T> Message type
 */
public abstract class RunnableConsumer<T> implements Runnable, Consumer<T>, StoppableConsumer<T> {
    // Push queue
    private final BlockingQueue<Produce<T>> queue = new LinkedBlockingQueue<>();

    // Processing methods to be inherited
    protected abstract void consumed(Producer<T> sender, T obj);

    protected abstract void stopped(Producer<T> by);

    protected abstract void interrupted();

    // Adds a message to the queue
    public void push(Producer<T> sender, T item) {
        queue.add(new Produce<>(sender, item, false));
    }

    // Stops the consumer
    public void stop(Producer<T> sender) {
        queue.add(new Produce<>(sender, null, true));
    }

    // Runs the consumer
    @Override
    public void run() {
        boolean run = true;
        while(run) { // Run until stop is received
            try {
                Produce<T> produce = queue.take();
                if(run = !produce.isStop())
                    consumed(produce.getSender(), produce.getItem()); // Process message
                else
                    stopped(produce.getSender()); // Process stop
            } catch(InterruptedException ex) {
                interrupted();
            }
        }
    }

    /**
     * Produce data structure
     * @param <T> Message type
     */
    class Produce<T> {
        private final Producer<T> sender;
        private final T item;
        private final boolean stop;

        public Producer<T> getSender() {
            return sender;
        }

        public T getItem() {
            return item;
        }

        public boolean isStop() {
            return stop;
        }

        public Produce(Producer<T> sender, T produce, boolean stop) {
            this.sender = sender;
            this.item = produce;
            this.stop = stop;
        }
    }
}
