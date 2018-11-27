package colourise.gui;

import colourise.networking.protocol.Message;
import colourise.synchronisation.Consumer;
import colourise.synchronisation.Producer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Lobby extends ProducerConsumerFrame<Message> {
    private final JLabel message = new JLabel("Waiting for players...");
    private final boolean leader;
    private final JLabel count;
    private final JLabel capacity = new JLabel("/5");
    private final JPanel panel = new JPanel(new GridLayout(3, 1));
    private final JPanel top = new JPanel(new FlowLayout());
    private final JButton button = new JButton("Start");
    private final JPanel middle = new JPanel(new BorderLayout());
    private final JPanel bottom = new JPanel();

    public Lobby(boolean leader, int players) {
        setTitle("Lobby");
        this.leader = leader;
        count = new JLabel("0");
        count.setFont(new Font(count.getFont().getName(), count.getFont().getStyle(), 30));
        setPlayers(players);
        button.addActionListener(this::clicked);
        top.add(button);
        panel.add(top);
        middle.add(message);
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

    private void setPlayers(int players) {
        count.setText(Integer.toString(players));
        if(leader && players >= 2)
            button.setEnabled(true);
    }

    public static void main(String[] args) {
        Lobby lobby = new Lobby(false, 0);
    }

    public void clicked(ActionEvent e) {
        try {
            getRequest().push(this, Message.Factory.start());
        }catch(InterruptedException ex) {
        }
    }

    @Override
    protected void consumed(Producer<Message> sender, Message message) {
        // Handle messages
        System.out.println(message.getCommand());
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
        }
    }
}
