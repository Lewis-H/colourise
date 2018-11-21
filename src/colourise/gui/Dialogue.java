package colourise.gui;

import javax.swing.*;
import java.awt.*;

public class Dialogue extends JFrame {
    private String host;
    private int port;
    private JLabel hostLabel = new JLabel("Host: ");
    private JLabel portLabel = new JLabel("Port: ");
    private JPanel labels = new JPanel(new GridLayout(2, 1));
    private JPanel fields = new JPanel(new GridLayout(2, 1));
    private JTextField hostField = new JTextField("127.0.0.1");
    private JTextField portField = new JTextField("9000");
    private JPanel top = new JPanel(new FlowLayout(FlowLayout.CENTER));
    private JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    private JPanel split = new JPanel(new BorderLayout());
    private JButton button = new JButton("Submit");

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public Dialogue() {
        super("Connect");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(top, BorderLayout.NORTH);
        top.add(split);
        split.add(labels, BorderLayout.CENTER);
        split.add(fields, BorderLayout.EAST);
        getContentPane().add(bottom, BorderLayout.SOUTH);
        hostLabel.setLabelFor(hostField);
        labels.add(hostLabel);
        hostField.setColumns(15);
        fields.add(hostField);
        portLabel.setLabelFor(portField);
        labels.add(portLabel);
        portField.setColumns(15);
        fields.add(portField);
        bottom.add(button);
        pack();
        setResizable(false);
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
