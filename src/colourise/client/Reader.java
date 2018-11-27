package colourise.client;

import colourise.networking.Connection;
import colourise.networking.DisconnectedException;
import colourise.networking.protocol.Command;
import colourise.networking.protocol.Message;
import colourise.networking.protocol.Parser;
import colourise.synchronisation.Consumer;
import colourise.synchronisation.RunnableProducer;
import colourise.synchronisation.StoppableConsumer;

public class Reader extends RunnableProducer<Message> {
    private final Connection connection;
    private Parser parser = new Parser();

    public Reader(Connection connection) {
        this.connection = connection;
    }

    @Override
    protected void produce(Consumer<Message> requester) {
        try {
            while (parser.getRemaining() != 0)
                parser.add(connection.read(parser.getRemaining()));
            requester.push(this, parser.create());
            parser.reset();
        } catch(DisconnectedException ex) {
            if(StoppableConsumer.class.isInstance(requester))
                StoppableConsumer.class.cast(requester).stop(this);
        }
    }

    @Override
    protected void stopped(Consumer<Message> by) {

    }

    @Override
    protected void interrupted() {

    }
}
