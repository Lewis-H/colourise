package colourise.gui;

import javax.swing.*;
import java.awt.*;

/**
 * Displays grid of coloured squares to show the Match
 */
public final class ColouredGrid extends JComponent {
    // Size of squares
    private int scale;
    // Number of rows and columns
    private static final int ROWS = 6, COLUMNS = 10;
    // Colour grid
    private final Color[][] grid = new Color[ROWS][COLUMNS];

    /**
     * Sets the square scale.
     * @param scale Square size
     */
    public void setScale(int scale) {
        this.scale = scale;
        Dimension dimension = new Dimension(12 * scale, 8 * scale);
        setPreferredSize(dimension);
        setSize(dimension);
    }

    /**
     * Gets the square scale.
     * @return Square size.
     */
    public int getScale() {
        return scale;
    }

    /**
     * ColouredGrid constructor.
     * @param scale Square size
     */
    public ColouredGrid(int scale) {
        setScale(scale);
        for(int r = 0; r < ROWS; r++)
            for(int c = 0; c < COLUMNS; c++)
                grid[r][c] = Color.LIGHT_GRAY;
    }

    /**
     * Gets the colour of a specified square.
     * @param row Row number
     * @param column Column number
     * @return Colour of the specified square
     */
    private Color getColour(int row, int column) {
        return grid[row][column];
    }

    /**
     * Sets the colour of a specified square.
     * @param row Row number
     * @param column Column number
     * @param colour Colour
     */
    public void setColour(int row, int column, Color colour) {
        grid[row][column] = colour;
    }

    /**
     * Paints the component
     * @param g
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        Stroke stroke = g2.getStroke();
        Stroke outline = new BasicStroke(1);
        for(int r = 0; r < ROWS; r++) {
            for(int c = 0; c < COLUMNS; c++) {
                g2.setStroke(stroke);
                g2.setColor(getColour(r, c));
                g2.fillRect((c + 1) * scale, (r + 1) * scale, scale, scale);
                g2.setColor(Color.BLACK);
                g2.setStroke(outline);
                g2.drawRect((c + 1) * scale,(r + 1) * scale, scale, scale);
            }
        }
    }
}
