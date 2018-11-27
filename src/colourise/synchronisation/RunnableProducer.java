package colourise.synchronisation;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class RunnableProducer<T> implements Runnable, Producer<T>, StoppableProducer<T> {
    private final BlockingQueue<Request<T>> requests  = new LinkedBlockingQueue<>();

    public void stop(Consumer<T> sender) {
        requests.add(new Request<>(sender, true));
    }

    public void request(Consumer<T> sender) { requests.add(new Request<>(sender, false)); }

    @Override
    public void run() {
        boolean run = true;
        while(run) {
            try {
                Request<T> request = requests.take();
                if(run = !request.isStop())
                    produce(request.getConsumer());
                else
                    stopped(request.getConsumer());
            } catch (InterruptedException ex) {
                interrupted();
            }
        }
    }

    protected abstract void produce(Consumer<T> requester);

    protected abstract void stopped(Consumer<T> by);

    protected abstract void interrupted();

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
