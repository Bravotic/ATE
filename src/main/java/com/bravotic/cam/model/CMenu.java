package com.bravotic.cam.model;

import java.util.ArrayList;
import java.util.List;

public class CMenu {
    protected int selected;

    protected List<CMenuItem> allItems;

    protected List<CMenuItem> activeItems;

    protected int pageSize;

    private final List<CMenuChangeListener> listeners;
    private final List<CMenuCloseListener> closeListeners;

    private String searchTerm;

    private CMenu parent;

    public CMenu() {
        selected = 0;
        allItems = new ArrayList<>();
        activeItems = allItems;
        searchTerm = "";
        listeners = new ArrayList<>();
        closeListeners = new ArrayList<>();
        pageSize = 1;
        parent = null;
    }

    /**
     * Adds a menu item to the current menu. Menu items are appended to the END of the menu.
     * @param c The menu item to add.
     */
    public void addMenuItem(CMenuItem c) {
        if (c.hasSubmenu) {
            c.getSubmenu().parent = this;
        }

        allItems.add(c);

        for (CMenuChangeListener l : listeners){
            l.CMenuItemAdded(c);
        }
    }

    public void addCMenuChangeListener(CMenuChangeListener cl) {
        listeners.add(cl);
    }

    public void addCMenuCloseListener(CMenuCloseListener cl) {
        closeListeners.add(cl);
    }

    public void removeCMenuChangeListener(CMenuChangeListener cl) {
        listeners.remove(cl);
    }

    protected void notifyItemSelected(CMenuItem sel, boolean keepMenuOpen) {
        for (CMenuChangeListener l : listeners){
            l.CMenuItemSelected(sel, keepMenuOpen);
        }

        if (!keepMenuOpen) {
            for (CMenuCloseListener l : closeListeners) {

            }

        }
    }

    /**
     * Sets the search filter for the menu. The menu will only then show options which pass the filter. To remove the
     * search filter, set it to a blank string "".
     * @param search The filter to use.
     */
    public void setSearchFilter(String search) {
        searchTerm = search;

        if (search.isEmpty()) {
            activeItems = allItems;
        }
        else {
            selected = 0;

            for (CMenuChangeListener l : listeners){
                l.SelectionChanged(selected);
            }

            activeItems = new ArrayList<>();

            String searchLower = search.toLowerCase();

            for (CMenuItem item : allItems) {
                int searchPtr = 0;
                String lowerLabel = item.label.toLowerCase();

                for (int i = 0; i < lowerLabel.length() && searchPtr < search.length(); i++) {
                    if (lowerLabel.charAt(i) == searchLower.charAt(searchPtr)) {
                        searchPtr++;
                    }
                }

                if (searchPtr == search.length()) {
                    activeItems.add(item);
                }

            }
        }

        for (CMenuChangeListener l : listeners){
            l.CMenuSearchTermChanged(search);
        }
    }

    /**
     * Sets the amount of menu items viewable per page. This is used for page up/down.
     * @param size How many menu options are shown on the current screen.
     */
    public void setPageSize(int size) {
        pageSize = size;
    }

    /**
     * Moves the selection down to the next menu item. This should be used when the user either scrolls down or hits the
     * down arrow.
     */
    public void selectNext() {
        if (selected < activeItems.size() - 1) {
            selected++;

            for (CMenuChangeListener l : listeners){
                l.SelectionChanged(selected);
            }
        }
    }

    /**
     * Moves the selection up to the previous menu item. This should be used when the user scrolls up or hits the up
     * arrow.
     */
    public void selectLast() {
        if (selected > 0) {
            selected--;

            for (CMenuChangeListener l : listeners){
                l.SelectionChanged(selected);
            }
        }
    }

    public void pageDown() {
        if (selected + pageSize > activeItems.size() - 1) {
            selected = activeItems.size() - 1;
        }
        else {
            selected += pageSize;
        }

        for (CMenuChangeListener l : listeners){
            l.SelectionChanged(selected);
        }
    }

    public void pageUp() {
        if (selected - pageSize < 0) {
            selected = 0;
        }
        else {
            selected -= pageSize;
        }

        for (CMenuChangeListener l : listeners){
            l.SelectionChanged(selected);
        }
    }

    /**
     * Gets the index of the item which is selected. Note that this does NOT return the actual menu item, just the
     * index at which it resides. If you want to get the menu item, use .get(getSelected()) or alternatively use a
     * listener.
     * @return The index of the selected menu item.
     */
    public int getSelected() {
        return selected;
    }

    /**
     * Returns the search term used for the current menu.
     * @return The search term.
     */
    public String getSearchTerm() {
        return searchTerm;
    }

    /**
     * Selects the menu item at the current index. This should be called after the user right clicks or hits enter.
     */
    public void select() {
        if (!activeItems.isEmpty()) {
            CMenuItem sel = activeItems.get(selected);
            this.notifyItemSelected(sel, sel.select());
        }
    }

    /**
     * Goes back in the menu. This should be called when the user hits right click, or backspace.
     */
    public void back() {
        ArrayList<CMenuChangeListener> toRemove = new ArrayList<>();
        for (CMenuChangeListener l : listeners){
            if (l.CMenuGoBack()) {
                toRemove.add(l);
            }
        }

        listeners.removeAll(toRemove);
    }

    /**
     * Gets the size of the menu items. Note that this may not reflect all menu items added in total, as when the search
     * filter is applied, this will return the size of the menu items after the search filter. Although get will also
     * only return those that pass the search filter.
     * @return The number of items in the menu.
     */
    public int size() {
        return activeItems.size();
    }

    public int maxSize() {
        return allItems.size();
    }

    /**
     * Get the menu item at the index specified. Note that this will not always return the same menu item for each
     * index, as search filters can change how items are ordered. This will take search filters into account.
     * @param i The index where the item is found.
     * @return The CMenuItem at that index.
     */
    public CMenuItem get(int i) {
        return activeItems.get(i);
    }

    /**
     * Refresh the contents of the menu. This is mainly used so that if the contents have changed (maybe it contains
     * a file listing or something that could've realistically changed, or potentially the list hasn't even been created
     * yet). This is intentionally package scoped as the only thing that should call this is the CMenuItem the menu
     * belongs to.
     */
    void refresh() {

    }
}
