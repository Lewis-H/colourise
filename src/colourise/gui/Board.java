package colourise.gui;

import colourise.networking.protocol.Card;
import colourise.networking.protocol.Message;
import colourise.state.match.Position;
import colourise.synchronisation.Producer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Map;

public final class Board extends ProducerConsumerFrame<Message> {
    // Displayed game grid
    private final ColouredGrid grid = new ColouredGrid(50);
    // Displayed player list
    private final PlayerList list;
    // Message indicating the player's turn
    private final JLabel turn = new JLabel("It's your turn!");
    // Content panel
    private final JPanel panel = new JPanel(new BorderLayout());
    // Reference to "this" for anonymous class
    public final Board self = this;
    // Identifier sent by server
    private final int identifier;
    // Card buttons
    private final Cards cards;
    // Position click reader
    private final MouseListener play = new MouseListener() {
        @Override
        public void mouseClicked(MouseEvent e) {}

        @Override
        public void mousePressed(MouseEvent e) {}

        @Override
        public void mouseReleased(MouseEvent e) {
            int row = Math.round(e.getY() / grid.getScale()) - 1;
            int column = Math.round(e.getX() / grid.getScale()) - 1;
            try {
                getRequest().push(self, Message.Factory.play(row, column, cards.getCard()));
            }catch(InterruptedException ex) {
            }
        }

        @Override
        public void mouseEntered(MouseEvent e) {}

        @Override
        public void mouseExited(MouseEvent e) {}
    };

    /**
     * Board constructor.
     * @param scale Scale of the board (size of squares)
     * @param id Player's identifier
     * @param count Count of players
     * @param positions Starting positions
     * @param spectate Whether this board is spectating only
     */
    public Board(int scale, int id, int count, Map<Integer, Position> positions, boolean spectate) {
        setTitle("Board");
        identifier = id;
        list = new PlayerList(scale, id, count);
        cards = new Cards();
        cards.setOpaque(false);
        grid.addMouseListener(play);
        // Initialise with starting squares
        for(Map.Entry<Integer, Position> position : positions.entrySet())
            grid.setColour(position.getValue().getRow(), position.getValue().getColumn(), Colours.getColour(position.getKey()));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 5, 5, 5));
        panel.add(turn, BorderLayout.NORTH);
        panel.add(grid, BorderLayout.WEST);
        panel.add(list, BorderLayout.EAST);
        // Spectators don't need cards
        if(!spectate)
            panel.add(cards, BorderLayout.SOUTH);
        turn.setVerticalAlignment(SwingConstants.CENTER);
        turn.setVisible(false);
        add(panel, BorderLayout.SOUTH);
        pack();
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    @Override
    protected void consumed(Producer<Message> sender, Message message) {
        switch(message.getCommand()) {
            case BEGIN:
                // Player 0's turn first
                turn(0);
                break;
            case PLAYED:
                // Update the grid with a played colour
                grid.setColour(message.getArgument(1), message.getArgument(2), Colours.getColour(message.getArgument(0)));
                if(message.getArgument(0) == identifier)
                    cards.used(Card.fromInt(message.getArgument(3)));
                turn(message.getArgument(4));
                repaint();
                break;
            case LEFT:
                // A player left, remove them from the list
                list.remove(message.getArgument(0));
                turn(message.getArgument(1));
                repaint();
                break;
            case END:
                // Determine the winner and show a dialogue
                int max = 0;
                int winner = 0;
                for(int i = 0; i < 5; i++) {
                    if (message.getArgument(i) >= max) {
                        winner = i;
                        max = message.getArgument(i);
                    }
                }
                String dialogue = Colours.getName(winner) + " is the winner!";
                JOptionPane.showMessageDialog(this, dialogue);
                break;
        }
    }

    /**
     * Set the players turn (toggles prompt message)
     * @param id
     */
    private void turn(int id) {
        if(id == identifier)
            turn.setVisible(true);
        else
            turn.setVisible(false);
    }
}
