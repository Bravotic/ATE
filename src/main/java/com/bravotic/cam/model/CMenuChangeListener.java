package com.bravotic.cam.model;

public interface CMenuChangeListener {
    void CMenuItemAdded(CMenuItem c);

    void SelectionChanged(int selected);

    void CMenuItemSelected(CMenuItem c, boolean keepMenuOpen);

    void CMenuSearchTermChanged(String term);

    boolean CMenuGoBack();
}
