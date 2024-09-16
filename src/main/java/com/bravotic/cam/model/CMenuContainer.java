package com.bravotic.cam.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class CMenuContainer implements CMenuChangeListener {
    private final Stack<CMenu> menus;
    private final List<CMenuContainerListener> listeners;

    private void notifyMenuOpened(String label, CMenu opened) {
        for (CMenuContainerListener l : listeners) {
            l.submenuOpened(label, opened);
        }
    }

    private void notifyMenuClosed(CMenu closed) {
        for (CMenuContainerListener l : listeners) {
            l.submenuClosed(closed);
        }
    }

    private void notifyMenuClosing() {
        for (CMenuContainerListener l : listeners) {
            l.menuWouldLikeToClose();
        }
    }

    public CMenuContainer(CMenu root) {
        menus = new Stack<>();
        menus.push(root);
        root.addCMenuChangeListener(this);
        listeners = new ArrayList<>();
    }

    public CMenu getTopMenu() {
        return menus.peek();
    }

    public void addListener(CMenuContainerListener l) {
        this.listeners.add(l);
    }


    @Override
    public void CMenuItemAdded(CMenuItem c) {

    }

    @Override
    public void SelectionChanged(int selected) {

    }

    @Override
    public void CMenuItemSelected(CMenuItem c, boolean keepMenuOpen) {
        if (c.hasSubmenu) {
            CMenu newMenu = c.getSubmenu();

            menus.push(newMenu);
            newMenu.addCMenuChangeListener(this);
            notifyMenuOpened(c.label, newMenu);
        }
        else if (!keepMenuOpen) {
            notifyMenuClosing();
        }
    }

    @Override
    public void CMenuSearchTermChanged(String term) {

    }

    @Override
    public boolean CMenuGoBack() {
        // Close instead of pop since we are at the root
        if (menus.size() == 1) {
            notifyMenuClosing();
        }
        // Otherwise just pop the current menu
        else {
            CMenu closed = menus.pop();
            closed.removeCMenuChangeListener(this);
            notifyMenuClosed(closed);
        }
        return false;
    }
}
