package colourise.synchronisation;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class RunnableConsumer<T> implements Runnable, Consumer<T>, StoppableConsumer<T> {
    private final BlockingQueue<Produce<T>> queue = new LinkedBlockingQueue<>();

    protected abstract void consumed(Producer<T> sender, T obj);

    protected abstract void stopped(Producer<T> by);

    protected abstract void interrupted();

    public void push(Producer<T> sender, T item) {
        queue.add(new Produce<>(sender, item, false));
    }

    public void stop(Producer<T> sender) {
        queue.add(new Produce<>(sender, null, true));
    }

    @Override
    public void run() {
        boolean run = true;
        while(run) {
            try {
                Produce<T> produce = queue.take();
                if(run = !produce.isStop())
                    consumed(produce.getSender(), produce.getItem());
                else
                    stopped(produce.getSender());
            } catch(InterruptedException ex) {
                interrupted();
            }
        }
    }

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
