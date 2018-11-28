package colourise.gui;

import java.awt.*;

public class Colours {
    private static final Color[] colours = {
            Color.RED,
            Color.MAGENTA,
            Color.YELLOW,
            Color.GREEN,
            Color.PINK
    };

    public static Color getColour(int id) {
        return colours[id];
    }

    public static String getName(int id) {
        switch(id) {
            case 0:
                return "red";
            case 1:
                return "magenta";
            case 2:
                return "yellow";
            case 3:
                return "green";
            case 4:
                return "pink";
            default:
                return "unknown";
        }
    }
}
