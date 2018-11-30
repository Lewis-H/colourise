package colourise.bot;

import colourise.client.MyPlayer;
import colourise.networking.Binder;
import colourise.networking.Connection;
import colourise.networking.DisconnectedException;
import colourise.networking.protocol.Card;
import colourise.networking.protocol.Message;
import colourise.networking.protocol.Parser;
import colourise.state.match.*;
import colourise.state.player.CardAlreadyUsedException;
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

    public void start() throws CardAlreadyUsedException, MatchFinishedException, NotPlayersTurnException, CannotPlayException, InvalidPositionException, DisconnectedException {
        write(Message.Factory.hello(false));
        while(true) {
            while (parser.getRemaining() != 0)
                parser.add(connection.read(parser.getRemaining()));
            process(parser.create());
            parser.reset();
        }
    }

    private void process(Message message) throws CardAlreadyUsedException, MatchFinishedException, NotPlayersTurnException, CannotPlayException, InvalidPositionException, DisconnectedException {
        System.out.println(message.getCommand());
        if(lobby == 0 && match == null) {
            switch(message.getCommand()) {
                case JOINED:
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
                    Map<Integer, Position> starts = new HashMap<>(count);
                    for(int i = 0; i < count; i++)
                        starts.put(i, new Position(message.getArgument(2 + 2 * i), message.getArgument(3 + 2 * i)));
                    match = new Match(starts);
                    for(Player player : match.getPlayers()) {
                        if(player.getIdentifier() == id) {
                            me = new MyPlayer(player);
                            break;
                        }
                    }
                    if(id == 0)
                        play();
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
                        if (player.getIdentifier() == id)
                            player.play(row, column, card);
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

    private void play() throws DisconnectedException {
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch(InterruptedException ex) {
        } finally {
            List<Position> positions = null;
            Card card = Card.NONE;
            if(me.getInternal().has(Card.FREEDOM)) {
                positions = scanFreedom();
                card = Card.FREEDOM;
            }
            if(me.getInternal().has(Card.REPLACEMENT) && (positions == null || positions.size() == 0)) {
                positions = scanReplacement();
                card = Card.REPLACEMENT;
            }
            if(positions == null || positions.size() == 0) {
                positions = scan();
                card = Card.NONE;
            }
            Random random = new Random();
            Position position = positions.get(random.nextInt(positions.size()));
            write(Message.Factory.play(position.getRow(), position.getColumn(), card));
        }
    }

    private int write(Message m) throws DisconnectedException {
        return connection.write(m.toBytes());
    }

    private List<Position> scanFreedom() {
        List<Position> positions = new ArrayList<>();
        for(int row = 0; row < match.getRows(); row++)
            for(int column = 0; column < match.getColumns(); column++)
                if(!match.occupied(row, column))
                    positions.add(new Position(row, column));
        return positions;
    }

    private List<Position> scanReplacement() {
        List<Position> positions = new ArrayList<>();
        for(int row = 0; row < match.getRows(); row++)
            for(int column = 0; column < match.getColumns(); column++)
                if(match.occupied(row, column) && match.adjacent(row, column, me.getInternal()))
                    positions.add(new Position(row, column));
        return positions;
    }

    private List<Position> scan() {
        List<Position> positions = new ArrayList<>();
        for(int row = 0; row < match.getRows(); row++)
            for(int column = 0; column < match.getColumns(); column++)
                if(!match.occupied(row, column) && match.adjacent(row, column, me.getInternal()))
                        positions.add(new Position(row, column));
        assert positions.size() > 0;
        return positions;
    }


}
