package colourise.gui;

import javax.swing.*;
import java.awt.*;

public final class ColouredGrid extends JComponent {
    private int scale;
    private static final int ROWS = 6, COLUMNS = 10;
    private final Color[][] grid = new Color[ROWS][COLUMNS];

    public void setScale(int scale) {
        this.scale = scale;
        Dimension dimension = new Dimension(12 * scale, 8 * scale);
        setPreferredSize(dimension);
        setSize(dimension);
    }

    public int getScale() {
        return scale;
    }

    public ColouredGrid(int scale) {
        setScale(scale);
        for(int r = 0; r < ROWS; r++)
            for(int c = 0; c < COLUMNS; c++)
                grid[r][c] = Color.LIGHT_GRAY;
    }

    private Color getColour(int row, int column) {
        return grid[row][column];
    }

    public void setColour(int row, int column, Color colour) {
        grid[row][column] = colour;
    }

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



    public static void main(String[] args) {
        ColouredGrid board = new ColouredGrid(50);
        board.setColour(0, 0, Colours.getColour(1));
        JFrame frame = new JFrame();
        frame.add(board);
        frame.pack();
        frame.setVisible(true);
        frame.setSize(515, 340);
    }
}
