package com.bravotic.cam.model;

public interface CMenuAction {
    /**
     * The specified item was selected.
     * @param item The menu item selected.
     * @return true if the menu should stay on screen, false if it should close.
     */
    boolean menuItemSelected(CMenuItem item);
}
