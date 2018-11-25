package colourise.gui;

import colourise.client.Player;

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
}
