package colourise.gui;

import colourise.networking.Binder;
import colourise.networking.Connection;

import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.text.NumberFormat;

public final class Dialogue extends JFrame {
    private final JLabel hostLabel = new JLabel("Host: ");
    private final JLabel portLabel = new JLabel("Port: ");
    private final JPanel labels = new JPanel(new GridLayout(2, 1));
    private final JPanel fields = new JPanel(new GridLayout(2, 1));
    private final JTextField hostField = new JTextField("127.0.0.1");
    private final JFormattedTextField portField;
    private final JPanel top = new JPanel(new FlowLayout(FlowLayout.CENTER));
    private final JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    private final JPanel split = new JPanel(new BorderLayout());
    private final JButton button = new JButton("Submit");

    public Dialogue() {
        super("Connect");
        portField = new JFormattedTextField(initNumberFormatter());
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
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
        button.addActionListener(this::buttonClick);
        bottom.add(button);
        pack();
        setResizable(false);
    }

    private JFormattedTextField.AbstractFormatter initNumberFormatter() {
        NumberFormat format = NumberFormat.getInstance();
        format.setGroupingUsed(false);
        NumberFormatter formatter = new NumberFormatter(format);
        formatter.setMinimum(0);
        formatter.setMaximum(65535);
        formatter.setAllowsInvalid(false);
        return formatter;
    }

    private void buttonClick(ActionEvent e) {
        String host = hostField.getText();
        int port = Integer.parseInt(portField.getText());
        try {
            Connection connection = Binder.connect(new InetSocketAddress(host, port));
            System.out.println("Connected!");
        } catch (IOException ex) {

        }
    }
}
