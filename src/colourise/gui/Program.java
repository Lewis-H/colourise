package colourise.gui;

import colourise.client.Game;
import colourise.networking.Connection;
import colourise.networking.protocol.Message;
import colourise.networking.protocol.Parser;

import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;

public class Program {
    public static void main(String[] args) {
        Dialogue dialogue = new Dialogue();
        dialogue.setLocationRelativeTo(null);
        dialogue.setVisible(true);
    }
}
