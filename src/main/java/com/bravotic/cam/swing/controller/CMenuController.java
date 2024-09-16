package com.bravotic.cam.swing.controller;

import com.bravotic.cam.model.CMenu;

import javax.swing.event.MouseInputListener;
import java.awt.event.*;

public class CMenuController implements MouseWheelListener, MouseInputListener, KeyListener {
    private CMenu menu;

    String search;
    public CMenuController(CMenu menu) {
        this.menu = menu;
        this.search = this.menu.getSearchTerm();
    }

    public void setCurrentMenu(CMenu menu) {
        this.menu = menu;
        this.search = this.menu.getSearchTerm();
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if ((e.getModifiersEx() & MouseWheelEvent.SHIFT_DOWN_MASK) != 0) {
            if (e.getWheelRotation() > 0) {
                menu.pageDown();
            }
            else if(e.getWheelRotation() < 0) {
                menu.pageUp();
            }
        }
        else {
            if (e.getWheelRotation() > 0) {
                menu.selectNext();
            }
            else if(e.getWheelRotation() < 0) {
                menu.selectLast();
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        switch(e.getButton()) {
            case 1:
                menu.back();
                break;
            case 3:
                menu.select();
                break;
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        int c = e.getKeyCode();
        char ch = e.getKeyChar();

        if (c == KeyEvent.VK_TAB) {
            if ((e.getModifiersEx() & KeyEvent.SHIFT_DOWN_MASK) != 0) {
                menu.selectLast();
            }
            else {
                menu.selectNext();
            }
        }
        else if (c == KeyEvent.VK_DOWN) {
            menu.selectNext();
        }
        else if(c == KeyEvent.VK_UP) {
            menu.selectLast();
        }
        else if (c == KeyEvent.VK_LEFT) {
            menu.back();
        }
        else if (c == KeyEvent.VK_BACK_SPACE) {
            if (search.isEmpty()) {
                menu.back();
            }
            else if ((e.getModifiersEx() & KeyEvent.SHIFT_DOWN_MASK) != 0) {
                menu.setSearchFilter("");
                search = "";
            }
            else {
                search = search.substring(0, search.length()-1);
                menu.setSearchFilter(search);
            }
        }
        else if (c == KeyEvent.VK_ENTER || c == KeyEvent.VK_RIGHT) {
            menu.select();
        }
        else if (ch >= 32 && ch <= 127) {
            search += ch;
            menu.setSearchFilter(search);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }
}
