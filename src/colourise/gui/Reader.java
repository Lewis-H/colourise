package colourise.gui;

import colourise.networking.Connection;
import colourise.networking.DisconnectedException;
import colourise.networking.protocol.Command;
import colourise.networking.protocol.Message;
import colourise.networking.protocol.Parser;
import colourise.synchronisation.Consumer;
import colourise.synchronisation.RunnableProducer;
import colourise.synchronisation.StoppableConsumer;

/**
 * Producer which reads from a Connection.
 */
public class Reader extends RunnableProducer<Message> {
    // Connection to read from
    private final Connection connection;
    // Parser
    private Parser parser = new Parser();

    /**
     * Reader constructor.
     * @param connection Connection to read from.
     */
    public Reader(Connection connection) {
        this.connection = connection;
    }

    /**
     * Produces messages from reading the connection.
     * @param requester The request to respond to.
     */
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
