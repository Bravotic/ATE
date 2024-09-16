package com.bravotic.ate.swing.controller;

import com.bravotic.ate.model.APrompt;

import java.awt.event.*;

/**
 * Processes input from an APrompt Swing view.
 */
public class APromptController implements KeyListener, MouseListener, MouseWheelListener {
    private APrompt model;

    /**
     * Constructs a new controller for a given prompt.
     * @param model The prompt to attach this controller to.
     */
    public APromptController(APrompt model) {
        this.model = model;
    }

    /**
     * Constructs a new controller without a given prompt. A prompt is assumed to be supplied using setModel.
     */
    public APromptController() {
        this.model = null;
    }

    /**
     * Sets the model that this controller is controlling.
     * @param model The model to be controlled.
     */
    public void setModel(APrompt model) {
        this.model = model;
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        char c = e.getKeyChar();
        int code = e.getKeyCode();

        if (code == KeyEvent.VK_UP) {
            model.last();
        }
        else if (code == KeyEvent.VK_DOWN) {
            model.next();
        }
        else if (e.getKeyChar() == '\t') {
            if ((e.getModifiersEx() & KeyEvent.SHIFT_DOWN_MASK) == 0) {
                model.next();
            }
            else {
                model.last();
            }
        }
        else if (c == 8) {
            model.delete();
        }
        else if (c == 10 || code == KeyEvent.VK_RIGHT) {
            model.submit();
        }
        else if (c == 27 || code == KeyEvent.VK_LEFT) {
            model.close();
        }
        else if (c >= 32 && c < 127) {
            model.append(c);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void mouseClicked(MouseEvent e) {
        switch(e.getButton()) {
            case 1:
                model.close();
                break;
            case 3:
                model.submit();
                break;
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

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
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (e.getWheelRotation() > 0) {
            model.next();
        }
        else if(e.getWheelRotation() < 0) {
            model.last();
        }
    }
}
