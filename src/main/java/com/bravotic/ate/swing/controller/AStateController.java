package com.bravotic.ate.swing.controller;

import com.bravotic.ate.model.AState;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Allows an AState to be controlled.
 */
public class AStateController implements MouseListener, KeyListener {

    private final AState state;

    /**
     * Constructs a new AStateController for the given state.
     * @param state The state which this controller should control.
     */
    public AStateController(AState state) {
        this.state = state;
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == 2) {
            state.flip();
            e.consume();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void keyTyped(KeyEvent e) {
        if (e.getKeyChar() == 27) {
            state.flip();
            e.consume();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
