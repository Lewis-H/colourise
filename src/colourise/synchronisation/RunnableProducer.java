package colourise.synchronisation;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Producer which runs on a separate thread with Runnable
 * @param <T> Message type
 */
public abstract class RunnableProducer<T> implements Runnable, Producer<T>, StoppableProducer<T> {
    private final BlockingQueue<Request<T>> requests  = new LinkedBlockingQueue<>(); // Request queue

    // Queues a stop request
    public void stop(Consumer<T> sender) {
        requests.add(new Request<>(sender, true));
    }

    // Queues a consumer request
    public void request(Consumer<T> sender) { requests.add(new Request<>(sender, false)); }

    // Runs the producer
    @Override
    public void run() {
        boolean run = true;
        while(run) { // Run until stop receive
            try {
                Request<T> request = requests.take(); // Take from queue
                if(run = !request.isStop())
                    produce(request.getConsumer()); // Process stop
                else
                    stopped(request.getConsumer()); // Process message
            } catch (InterruptedException ex) {
                interrupted();
            }
        }
    }

    // Processing methods to be overridden
    protected abstract void produce(Consumer<T> requester);

    protected abstract void stopped(Consumer<T> by);

    protected abstract void interrupted();

    /**
     * Request data structure
     * @param <T> Message type
     */
    class Request<T> {
        private final Consumer<T> consumer;
        private final boolean stop;

        Request(Consumer<T> consumer, boolean stop) {
            this.consumer = consumer;
            this.stop = stop;
        }

        public Consumer<T> getConsumer() {
            return consumer;
        }

        public boolean isStop() {
            return stop;
        }
    }
}
