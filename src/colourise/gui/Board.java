package colourise.gui;

import javax.swing.*;
import java.awt.*;

public class Board extends JFrame {
    private ColouredGrid grid = new ColouredGrid(50);
    private PlayerList list = new PlayerList(25, 0, 5);
    private JPanel panel = new JPanel();

    public Board() {
        super("Client");
        panel.add(grid, BorderLayout.WEST);
        panel.add(list, BorderLayout.EAST);
        add(panel);
    }

    public static void main(String[] args) {
        Board game = new Board();
        game.setVisible(true);
        game.setResizable(false);
        game.pack();
    }
}
