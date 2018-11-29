package colourise.bot;

import colourise.client.MyPlayer;
import colourise.networking.Binder;
import colourise.networking.Connection;
import colourise.networking.DisconnectedException;
import colourise.networking.protocol.Card;
import colourise.networking.protocol.Message;
import colourise.networking.protocol.Parser;
import colourise.state.match.*;
import colourise.state.player.Player;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Bot {
    private Connection connection;
    // Count lobby connections
    private int lobby = 0;
    private boolean leader = false;
    private Match match;
    private MyPlayer me;
    private final Parser parser = new Parser();

    public Bot(String host, int port) throws IOException {
        connection = Binder.connect(new InetSocketAddress(host, port));
    }

    public void start() throws MatchFinishedException, NotPlayersTurnException, CannotPlayException, InvalidPositionException, DisconnectedException {
        while(true) {
            while (parser.getRemaining() != 0)
                parser.add(connection.read(parser.getRemaining()));
            process(parser.create());
            parser.reset();
        }
    }

    private void process(Message message) throws MatchFinishedException, NotPlayersTurnException, CannotPlayException, InvalidPositionException, DisconnectedException {
        if(lobby == 0 && match == null) {
            switch(message.getCommand()) {
                case HELLO:
                    lobby = message.getArgument(0);
                    break;
            }
        } else if(lobby != 0) {
            switch(message.getCommand()) {
                case LEAD:
                    leader = true;
                    break;
                case JOINED:
                    lobby++;
                    break;
                case LEFT:
                    lobby--;
                    break;
                case BEGIN:
                    lobby = 0;
                    int id = message.getArgument(0);
                    int count = message.getArgument(1);
                    match = new Match(count);
                    for(Player player : match.getPlayers()) {
                        if(player.getIdentifier() == id) {
                            me = new MyPlayer(player);
                            break;
                        }
                    }
                    break;
            }
        } else if(match != null) {
            int id;
            int next;
            switch(message.getCommand()) {
                case PLAYED:
                    id = message.getArgument(0);
                    int row = message.getArgument(1);
                    int column = message.getArgument(2);
                    Card card = Card.fromInt(message.getArgument(3));
                    next = message.getArgument(4);
                    for(Player player : match.getPlayers())
                        if(player.getIdentifier() == id)
                            match.play(row, column, player, card);
                    if(next == me.getInternal().getIdentifier())
                        play();
                    break;
                case LEFT:
                    id = message.getArgument(0);
                    next = message.getArgument(1);
                    for(Player player : match.getPlayers())
                        if(player.getIdentifier() == id)
                            player.leave();
                    if(next == me.getInternal().getIdentifier())
                        play();
                    break;
            }
        }
    }

    private void play() throws MatchFinishedException, NotPlayersTurnException, CannotPlayException, InvalidPositionException, DisconnectedException {
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch(InterruptedException ex) {
        } finally {
            List<Position> positions = scan();
            Random random = new Random();
            Position position = positions.get(random.nextInt(positions.size()));
            match.play(position.getRow(), position.getColumn(), me.getInternal(), Card.NONE);
            write(Message.Factory.play(position.getRow(), position.getColumn(), Card.NONE));
        }
    }

    private int write(Message m) throws DisconnectedException {
        return connection.write(m.toBytes());
    }

    private List<Position> scan() {
        List<Position> positions = new ArrayList<>();
        for(int row = 0; row < match.getRows(); row++)
            for(int column = 0; column < match.getColumns(); column++)
                if(match.get(row, column) == me.getInternal())
                    if(!match.blocked(row, column))
                        positions.add(new Position(row, column));
        assert positions.size() > 0;
        return positions;
    }


}
