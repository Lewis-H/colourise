package colourise.gui;

import javax.swing.*;
import java.awt.*;

public class Game extends JFrame {
    private ColouredGrid grid = new ColouredGrid(50);
    private PlayerList list = new PlayerList(25, 0, 5);
    private JPanel panel = new JPanel();

    public Game() {
        super("Client");
        panel.add(grid, BorderLayout.WEST);
        panel.add(list, BorderLayout.EAST);
        add(panel);
    }

    public static void main(String[] args) {
        Game game = new Game();
        game.setVisible(true);
        game.pack();
    }
}
