package colourise.gui;

import colourise.client.Game;
import colourise.client.Stage;
import colourise.networking.Binder;
import colourise.networking.Connection;
import colourise.networking.DisconnectedException;
import colourise.networking.protocol.Message;
import colourise.networking.protocol.Parser;

import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

    public String getHost() {
        return hostField.getText();
    }

    public int getPort() {
        return Integer.parseInt(portField.getText());
    }

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
        button.addActionListener(this::clicked);
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

    private void clicked(ActionEvent e) {
        try {
            Connection connection = Binder.connect(new InetSocketAddress(getHost(), getPort()));
            new Thread(new Controller(connection)).start();
            setVisible(false);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }
}
