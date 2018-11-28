package colourise.gui;

import colourise.networking.protocol.Card;
import colourise.networking.protocol.Message;
import colourise.synchronisation.Producer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

public class Board extends ProducerConsumerFrame<Message> {
    private final ColouredGrid grid = new ColouredGrid(50);
    private final PlayerList list;
    private final JPanel panel = new JPanel();
    public final Board self = this;
    private final int identifier;
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
                getRequest().push(self, Message.Factory.play(row, column, Card.NONE));
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
        identifier = id;
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
            case BEGIN:
                turn(0);
                break;
            case PLAYED:
                grid.setColour(message.getArgument(1), message.getArgument(2), Colours.getColour(message.getArgument(0)));
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
                ArrayList<Integer> winners = new ArrayList<>();
                for(int i = 0; i < list.count(); i++) {
                    if(message.getArgument(i) > max) {
                        max = message.getArgument(i);
                        winners.clear();
                        winners.add(i);
                    } else if(message.getArgument(i) == max) {
                        winners.add(i);
                    }
                }
                String dialogue = "";
                for(int i = 0; i < winners.size(); i++)
                    dialogue += (i == winners.size() - 1 ? (i == 0 ? "" : ", ") : " and ") + Colours.getName(winners.get(i));
                dialogue += (winners.size() == 1 ? " is " : " are ") + "the winner" + (winners.size() == 1 ? "!" : " s!");
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
