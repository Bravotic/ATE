package com.bravotic.cam.model;

public class CMenuItem {
    public final String label;
    public final boolean hasSubmenu;

    private CMenuAction action;

    private CMenu submenu;

    public CMenuItem(String label, CMenuAction action) {
        this.label = label;
        this.hasSubmenu = false;
        this.submenu = null;
        this.action = action;
    }

    public CMenuItem(String label) {
        this.label = label;
        this.hasSubmenu = false;
        this.submenu = null;
        this.action = null;
    }

    public CMenuItem(String label, CMenu submenu) {
        this.label = label;
        this.hasSubmenu = true;
        this.submenu = submenu;
    }

    public CMenu getSubmenu() {
        return submenu;
    }

    public void setSubmenu(CMenu submenu) {
        if (this.hasSubmenu) {
            this.submenu = submenu;
        }
    }

    public boolean select() {
        if(hasSubmenu) {
            submenu.refresh();
            return true;
        }
        else if (action != null) {
            return action.menuItemSelected(this);
        }
        else {
            return true;
        }
    }


}
