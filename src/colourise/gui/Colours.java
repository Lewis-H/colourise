package colourise.gui;

import java.awt.*;

public class Colours {
    /**
     * All possible Colours in the game.
     */
    private static final Color[] colours = {
            Color.RED,
            Color.MAGENTA,
            Color.YELLOW,
            Color.GREEN,
            Color.PINK
    };

    /**
     * Converts an identifier into a colour.
     * @param id Identifier
     * @return Colour
     */
    public static Color getColour(int id) {
        return colours[id];
    }

    /**
     * Gets the name of the colour for the specified identifier.
     * @param id Identifier
     * @return Colour name
     */
    public static String getName(int id) {
        switch(id) {
            case 0:
                return "Red";
            case 1:
                return "Magenta";
            case 2:
                return "Yellow";
            case 3:
                return "Green";
            case 4:
                return "Pink";
            default:
                return "Unknown";
        }
    }
}
