package colourise.gui;

import javax.swing.*;
import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class PlayerList extends JComponent {
    private Set<Integer> players = new HashSet<Integer>(5);
    private int scale;
    private final int id;

    public void setScale(int scale) {
        this.scale = scale;
        Dimension dimension = new Dimension(scale * 3, scale * 2 * 5);
        setPreferredSize(dimension);
        setSize(dimension);
    }

    public PlayerList(int scale, int id, int count) {
        setScale(scale);
        this.id = id;
        for(int i = 0; i < count; i++)
            players.add(i);
    }

    public void remove(int id) {
        players.remove(id);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        Stroke stroke = g2.getStroke();
        Stroke outline = new BasicStroke(3);
        int i = 0;
        for(Integer identifier : players) {
            g2.setColor(Colours.getColour(identifier));
            g2.fillRect(scale, i * scale * 2, scale, scale);
            g2.setColor(identifier == id ? Color.BLUE : Color.BLACK);
            g2.setStroke(outline);
            g2.drawRect(scale, i * scale * 2, scale, scale);
            g2.setStroke(stroke);
            i++;
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        PlayerList players = new PlayerList(25, 0, 5);
        frame.add(players);
        frame.pack();
        frame.setVisible(true);
    }
}
