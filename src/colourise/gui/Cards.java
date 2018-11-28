package colourise.gui;

import colourise.networking.protocol.Card;

import javax.swing.*;
import java.awt.*;

public final class Cards extends JPanel {
    private final JButton freedom = new JButton("Freedom");
    private final JButton doubleMove = new JButton("Double Move");
    private final JButton replacement = new JButton("Replacement");
    private final Color normal;
    private Card card = Card.NONE;

    public Cards() {
        normal = freedom.getBackground();
        freedom.addActionListener(e -> {
            if(freedom.getBackground() == normal) {
                card = Card.FREEDOM;
                freedom.setBackground(Color.ORANGE);
                doubleMove.setBackground(normal);
                replacement.setBackground(normal);
            }else{
                freedom.setBackground(Color.ORANGE);
                card = Card.NONE;
            }
        });
        doubleMove.addActionListener(e -> {
            if(doubleMove.getBackground() == normal) {
                card = Card.DOUBLE_MOVE;
                doubleMove.setBackground(Color.ORANGE);
                freedom.setBackground(normal);
                replacement.setBackground(normal);
            }else{
                doubleMove.setBackground(Color.ORANGE);
            }
        });
        replacement.addActionListener(e -> {
            if(replacement.getBackground() == normal) {
                card = Card.REPLACEMENT;
                replacement.setBackground(Color.ORANGE);
                freedom.setBackground(normal);
                doubleMove.setBackground(normal);
            }else{
                replacement.setBackground(Color.ORANGE);
            }
        });
        add(freedom);
        add(doubleMove);
        add(replacement);
    }

    public Card getCard() {
        return card;
    }

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
