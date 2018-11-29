package colourise.gui;

import colourise.networking.protocol.Card;
import colourise.networking.protocol.Message;
import colourise.state.match.Position;
import colourise.synchronisation.Producer;
import javafx.geometry.Pos;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Map;

public class Board extends ProducerConsumerFrame<Message> {
    private final ColouredGrid grid = new ColouredGrid(50);
    private final PlayerList list;
    private final JPanel panel = new JPanel();
    public final Board self = this;
    private final int identifier;
    private final Cards cards;
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
                System.out.println("row: " + row + ", column:" + column);
                getRequest().push(self, Message.Factory.play(row, column, cards.getCard()));
            }catch(InterruptedException ex) {
            }
        }

        @Override
        public void mouseEntered(MouseEvent e) {}

        @Override
        public void mouseExited(MouseEvent e) {}
    };

    public Board(int scale, int id, int count, Map<Integer, Position> positions) {
        setTitle("Board");
        identifier = id;
        list = new PlayerList(scale, id, count);
        cards = new Cards();
        grid.addMouseListener(play);
        for(Map.Entry<Integer, Position> position : positions.entrySet())
            grid.setColour(position.getValue().getRow(), position.getValue().getColumn(), Colours.getColour(position.getKey()));
        panel.add(grid, BorderLayout.WEST);
        panel.add(list, BorderLayout.EAST);
        panel.add(cards, BorderLayout.SOUTH);
        add(panel);
        pack();
        setResizable(false);
    }

    @Override
    protected void consumed(Producer<Message> sender, Message message) {
        switch(message.getCommand()) {
            case BEGIN:
                turn(0);
                break;
            case PLAYED:
                grid.setColour(message.getArgument(1), message.getArgument(2), Colours.getColour(message.getArgument(0)));
                if(message.getArgument(0) == identifier)
                    cards.used(Card.fromInt(message.getArgument(3)));
                turn(message.getArgument(4));
                repaint();
                break;
            case LEFT:
                list.remove(message.getArgument(0));
                turn(message.getArgument(1));
                repaint();
                break;
            case END:
                int max = 0;
                int winner = 0;
                for(int i = 0; i < list.count(); i++)
                    if(message.getArgument(i) >= max)
                        winner = i;
                String dialogue = Colours.getName(winner) + " is the winner!";
                char[] ch = dialogue.toCharArray();
                ch[0] = String.valueOf(ch[0]).toUpperCase().charAt(0);
                dialogue = String.valueOf(ch);
                JOptionPane.showMessageDialog(this, dialogue);
                break;
        }
    }

    private void turn(int id) {
        if(id == identifier)
            panel.setBackground(Colours.getColour(identifier));
        else
            panel.setBackground(Color.WHITE);
    }
}
