package colourise.gui;

import colourise.networking.protocol.Message;
import colourise.synchronisation.Consumer;
import colourise.synchronisation.Producer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Lobby frame.
 */
public class Lobby extends ProducerConsumerFrame<Message> {
    // Message label
    private final JLabel message = new JLabel("Waiting for players...", SwingConstants.CENTER);
    // Whether we are the leader
    private boolean leader;
    // How many players
    private int players;
    // Display of count of players
    private final JLabel count;
    // Maximum players
    private final JLabel capacity = new JLabel("/5");
    // Panels for layout
    private final JPanel panel = new JPanel(new GridLayout(3, 1));
    private final JPanel top = new JPanel(new FlowLayout());
    private final JButton button = new JButton("Start");
    private final JPanel middle = new JPanel(new BorderLayout());
    private final JPanel bottom = new JPanel();

    public Lobby(int players) {
        setTitle("Lobby");
        leader = false;
        count = new JLabel("1");
        count.setFont(new Font(count.getFont().getName(), count.getFont().getStyle(), 30));
        setPlayers(players);
        button.addActionListener(this::clicked);
        top.add(button);
        panel.add(top);
        middle.add(message, BorderLayout.CENTER);
        panel.add(middle);
        button.setEnabled(false);
        bottom.add(count);
        bottom.add(capacity);
        panel.add(bottom);
        add(panel);
        pack();
        setSize(300, 200);
        setResizable(false);
    }

    /**
     * Sets us as the leader.
     * @param leader Whether we are the leader
     */
    public void setLeader(boolean leader) {
        this.leader = leader;
        // Leader can start the game once two players are connected
        if(leader && players >= 2)
            button.setEnabled(true);
    }

    /**
     * Set the number of players
     * @param players Number of players
     */
    private void setPlayers(int players) {
        this.players = players;
        count.setText(Integer.toString(players));
        if(leader && players >= 2)
            button.setEnabled(true);
    }

    /**
     * Start button press.
     * @param e Event argument
     */
    public void clicked(ActionEvent e) {
        try {
            getRequest().push(this, Message.Factory.start());
        }catch(InterruptedException ex) {
        }
    }

    /**
     * Messages received from Controller forwarding.
     * @param sender Sending producer
     * @param message Message
     */
    @Override
    protected void consumed(Producer<Message> sender, Message message) {
        // Handle messages
        switch(message.getCommand()) {
            case JOINED:
                setPlayers(message.getArgument(0));
                break;
            case LEFT:
                setPlayers(message.getArgument(0));
                break;
            case BEGIN:
                setVisible(false);
                break;
            case LEAD:
                setLeader(true);
                break;
        }
    }
}
