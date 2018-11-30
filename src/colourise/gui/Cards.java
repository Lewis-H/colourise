package colourise.gui;

import colourise.networking.protocol.Card;

import javax.swing.*;
import java.awt.*;

/**
 * Card display
 */
public final class Cards extends JPanel {
    // Freedom card button
    private final JButton freedom = new JButton("Freedom");
    // Double move card button
    private final JButton doubleMove = new JButton("Double Move");
    // Replacement card button
    private final JButton replacement = new JButton("Replacement");
    // Normal button colour
    private final Color normal;
    // Card in use
    private Card card = Card.NONE;

    public Cards() {
        normal = freedom.getBackground();
        // Button listeners
        freedom.addActionListener(e -> {
            if(freedom.getBackground() == normal) {
                card = Card.FREEDOM;
                freedom.setBackground(Color.ORANGE);
                doubleMove.setBackground(normal);
                replacement.setBackground(normal);
            }else{
                card = Card.NONE;
                freedom.setBackground(normal);
            }
        });
        doubleMove.addActionListener(e -> {
            if(doubleMove.getBackground() == normal) {
                card = Card.DOUBLE_MOVE;
                doubleMove.setBackground(Color.ORANGE);
                freedom.setBackground(normal);
                replacement.setBackground(normal);
            }else{
                card = Card.NONE;
                doubleMove.setBackground(normal);
            }
        });
        replacement.addActionListener(e -> {
            if(replacement.getBackground() == normal) {
                card = Card.REPLACEMENT;
                replacement.setBackground(Color.ORANGE);
                freedom.setBackground(normal);
                doubleMove.setBackground(normal);
            }else{
                card = Card.NONE;
                replacement.setBackground(normal);
            }
        });
        add(freedom);
        add(doubleMove);
        add(replacement);
    }

    public Card getCard() {
        return card;
    }

    // Mark card as used (disable button)
    public void used(Card card) {
        this.card = Card.NONE;
        if(card == Card.FREEDOM) {
            freedom.setEnabled(false);
            freedom.setBackground(normal);
        }else if(card == Card.REPLACEMENT) {
            replacement.setEnabled(false);
            replacement.setBackground(normal);
        }else if(card == Card.DOUBLE_MOVE) {
            doubleMove.setEnabled(false);
            doubleMove.setBackground(normal);
        }
    }


}
