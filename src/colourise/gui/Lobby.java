package colourise.gui;

import colourise.networking.protocol.Message;
import colourise.synchronisation.Consumer;
import colourise.synchronisation.Producer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Lobby extends JFrame implements Producer<Message>, Consumer<Message> {
    private final JLabel message = new JLabel("Waiting for players...");
    private int players;
    private final boolean leader;
    private final JLabel count;
    private final JLabel capacity = new JLabel("/5");
    private final JPanel panel = new JPanel(new GridLayout(3, 1));
    private final JPanel top = new JPanel(new FlowLayout());
    private final JButton button = new JButton("Start");
    private final JPanel middle = new JPanel(new BorderLayout());
    private final JPanel bottom = new JPanel();
    private BlockingQueue<Consumer<Message>> requests = new LinkedBlockingQueue<>();

    public Lobby(boolean leader, int players) {
        super("Lobby");
        this.leader = leader;
        this.players = players;
        count = new JLabel(Integer.toString(this.players));
        button.addActionListener(this::clicked);
        top.add(button);
        panel.add(top);
        middle.add(message);
        panel.add(middle);
        count.setFont(new Font(count.getFont().getName(), count.getFont().getStyle(), 30));
        bottom.add(count);
        bottom.add(capacity);
        panel.add(bottom);
        add(panel);
        pack();
        setSize(300, 200);
        setResizable(false);
    }

    public void increment() {
        count.setText(Integer.toString(++players));
        if(leader && players >= 2)
            button.setEnabled(true);
    }

    public void decrement() {
        count.setText(Integer.toString(--players));
        if(leader && players < 2)
            button.setEnabled(false);
    }

    public static void main(String[] args) {
        Lobby lobby = new Lobby(false, 0);
    }

    public void clicked(ActionEvent e) {
        try {
            requests.take().push(this, Message.Factory.start());
        }catch(InterruptedException ex) {
        }
    }

    @Override
    public void request(Consumer<Message> sender) {
        requests.add(sender);
    }

    @Override
    public void push(Producer<Message> sender, Message message) {
        SwingUtilities.invokeLater(() -> {
            // Handle messages
        });
    }
}
