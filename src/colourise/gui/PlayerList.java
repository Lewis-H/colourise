package colourise.gui;

import javax.swing.*;
import java.awt.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Display of player list (coloured squares)
 */
public class PlayerList extends JComponent {
    // Players available
    private Set<Integer> players = new HashSet<Integer>(5);
    // Square size
    private int scale;
    // Our identifier
    private final int id;

    /**
     * Gets the count of players.
     * @return Count of players
     */
    public int count() {
        return players.size();
    }

    /**
     * Sets the scale of the squares.
     * @param scale Scale of the squares
     */
    public void setScale(int scale) {
        this.scale = scale;
        Dimension dimension = new Dimension(scale * 3, scale * 2 * 5);
        setPreferredSize(dimension);
        setSize(dimension);
    }

    /**
     * PlayerList constuctor
     * @param scale Square scale
     * @param id Identifier
     * @param count Player count
     */
    public PlayerList(int scale, int id, int count) {
        setScale(scale);
        this.id = id;
        for(int i = 0; i < count; i++)
            players.add(i);
    }

    /**
     * Removes a player by their identifier
     * @param id Identifier of player to remove
     */
    public void remove(int id) {
        players.remove(id);
    }

    /**
     * Draws this component
     * @param g Graphics
     */
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
}
