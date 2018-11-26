package colourise.gui;

import javax.swing.*;
import java.awt.*;

public class Lobby extends JFrame {
    private final JLabel message = new JLabel("Waiting for players...");
    private int players = 0;
    private final boolean leader;
    private final JLabel count = new JLabel(Integer.toString(players));
    private final JLabel capacity = new JLabel("/5");
    private final JPanel panel = new JPanel(new GridLayout(3, 1));
    private final JPanel top = new JPanel(new FlowLayout());
    private final JButton button = new JButton("Start");
    private final JPanel middle = new JPanel(new BorderLayout());
    private final JPanel bottom = new JPanel();

    public Lobby(boolean leader) {
        super("Lobby");
        this.leader = leader;
        top.add(button);
        panel.add(top);
        middle.add(message);
        panel.add(middle);
        count.setFont(new Font(count.getFont().getName(), count.getFont().getStyle(), 30));
        bottom.add(count);
        bottom.add(capacity);
        panel.add(bottom);
        add(panel);
        setVisible(true);
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
        Lobby lobby = new Lobby(false);
        lobby.setLocationRelativeTo(null);
    }
}
