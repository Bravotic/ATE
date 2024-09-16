package com.bravotic.ate.swing.controller;

import com.bravotic.ate.model.ADocument;

import java.awt.*;
import java.awt.event.*;

/**
 * Processes input from an ADocumentView Swing view.
 */
public class ADocumentController implements KeyListener, MouseListener {

    private final ADocument doc;

    /**
     * Constructs a new controller for the given document.
     * @param doc The document to attach the controller to.
     */
    public ADocumentController(ADocument doc) {
        this.doc = doc;
    }


    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        char keyChar = e.getKeyChar();
        int code = e.getKeyCode();

        if (keyChar == '\n') {
            // TODO: Make this its own function
            this.doc.insert('\n');
        }
        else if (keyChar == '\t') {
            this.doc.insert('\t');
        }
        else if (keyChar == 8) {
            this.doc.remove();
        }
        else if(code == KeyEvent.VK_LEFT) {
            doc.cursorMove(ADocument.CursorDirection.CursorLeft);
        }
        else if(code == KeyEvent.VK_RIGHT) {
            doc.cursorMove(ADocument.CursorDirection.CursorRight);
        }
        else if(code == KeyEvent.VK_UP) {
            doc.cursorMove(ADocument.CursorDirection.CursorUp);
        }
        else if(code == KeyEvent.VK_DOWN) {
            doc.cursorMove(ADocument.CursorDirection.CursorDown);
        }
        else if(code == KeyEvent.VK_PAGE_DOWN) {
            doc.pageDown();

        }
        else if (code == KeyEvent.VK_PAGE_UP) {
            doc.pageUp();
        }
        else if (code == KeyEvent.VK_DELETE) {
            // TODO: implement delete
        }
        else if (keyChar >= 32 && keyChar != 65535){
            this.doc.insert(e.getKeyChar());
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void mouseClicked(MouseEvent e) {
        Point pos = e.getPoint();
        doc.clickAt(pos.x - 5, pos.y - 5);
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
}
