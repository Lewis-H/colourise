package colourise.gui;

import colourise.networking.protocol.Card;
import colourise.networking.protocol.Message;
import colourise.synchronisation.Producer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class Board extends ProducerConsumerFrame<Message> {
    private final ColouredGrid grid = new ColouredGrid(50);
    private final PlayerList list;
    private final JPanel panel = new JPanel();
    public final Board self = this;
    private final MouseListener play = new MouseListener() {
        @Override
        public void mouseClicked(MouseEvent e) {}

        @Override
        public void mousePressed(MouseEvent e) {}

        @Override
        public void mouseReleased(MouseEvent e) {
            int column = Math.round(e.getX() / grid.getScale()) - 1;
            int row = Math.round(e.getY() / grid.getScale()) - 1;
            try {
                System.out.println("row: " + row + ", column:" + column);
                getRequest().push(self, Message.Factory.play(row, row, Card.NONE));
            }catch(InterruptedException ex) {
            }
        }

        @Override
        public void mouseEntered(MouseEvent e) {}

        @Override
        public void mouseExited(MouseEvent e) {}
    };

    public Board(int scale, int id, int count) {
        setTitle("Board");
        list = new PlayerList(scale, id, count);
        grid.addMouseListener(play);
        panel.add(grid, BorderLayout.WEST);
        panel.add(list, BorderLayout.EAST);
        add(panel);
        pack();
        setResizable(false);
    }

    public static void main(String[] args) {
        Board game = new Board(25, 0, 5);
        game.setVisible(true);
    }

    @Override
    protected void consumed(Producer<Message> sender, Message message) {
        switch(message.getCommand()) {
            case PLAYED:
                grid.setColour(message.getArgument(1), message.getArgument(2), Colours.getColour(message.getArgument(0)));
                repaint();
                break;
        }
    }
}
