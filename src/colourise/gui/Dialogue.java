package colourise.gui;

import colourise.networking.Binder;
import colourise.networking.Connection;
import colourise.networking.DisconnectedException;

import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.text.NumberFormat;

/**
 * Host input dialogue
 */
public final class Dialogue extends JFrame {
    // Host label
    private final JLabel hostLabel = new JLabel("Host: ");
    // Port label
    private final JLabel portLabel = new JLabel("Port: ");
    // Label grid
    private final JPanel labels = new JPanel(new GridLayout(2, 1));
    // Field grid
    private final JPanel fields = new JPanel(new GridLayout(2, 1));
    // Host input field (default localhost)
    private final JTextField hostField = new JTextField("127.0.0.1");
    // Formatted port field (numbers only)
    private final JFormattedTextField portField;
    // Panels for layout
    private final JPanel middle = new JPanel(new FlowLayout(FlowLayout.CENTER));
    private final JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    private final JPanel split = new JPanel(new BorderLayout());
    // Connect button
    private final JButton button = new JButton("Connect");
    // Spectator checkbox
    private final JCheckBox spectate = new JCheckBox("Spectate", false);

    /**
     * Gets the host collected by this dialogue
     * @return
     */
    public String getHost() {
        return hostField.getText();
    }

    /**
     * Gets the port collected by this dialogue
     * @return
     */
    public int getPort() {
        return Integer.parseInt(portField.getText());
    }

    /**
     * Dialogue constructor
     */
    public Dialogue() {
        super("Connect");
        portField = new JFormattedTextField(initNumberFormatter());
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(middle, BorderLayout.CENTER);
        middle.add(split);
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
        bottom.add(spectate, BorderLayout.WEST);
        button.addActionListener(this::clicked);
        bottom.add(button, BorderLayout.EAST);
        pack();
        setSize(250, getHeight());
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    /**
     * Initialises the number formatter for the formatted text field.
     * @return
     */
    private JFormattedTextField.AbstractFormatter initNumberFormatter() {
        NumberFormat format = NumberFormat.getInstance();
        format.setGroupingUsed(false);
        NumberFormatter formatter = new NumberFormatter(format);
        formatter.setMinimum(0);
        formatter.setMaximum(65535);
        formatter.setAllowsInvalid(false);
        return formatter;
    }

    /**
     * Processes the connect button click.
     * @param e Event argument
     */
    private void clicked(ActionEvent e) {
        try {
            Connection connection = Binder.connect(new InetSocketAddress(getHost(), getPort()));
            new Thread(new Controller(connection, spectate.isSelected())).start();
            setVisible(false);
        } catch(IOException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        } catch(DisconnectedException ex) {
            JOptionPane.showMessageDialog(this, "The server unexpectedly closed the connection.");
        }
    }
}
