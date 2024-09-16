package com.bravotic.cam.vtlib.view;

import com.bravotic.cam.model.CMenu;
import com.bravotic.cam.model.CMenuChangeListener;
import com.bravotic.cam.model.CMenuItem;
import com.bravotic.vtlib.VT;
import com.bravotic.vtlib.Position;

public class CMenuView implements CMenuChangeListener {
    private final CMenu model;
    private int previouslySelected;
    private Position searchCursor;
    private Position upperCorner;

    private final VT vt;

    private int offset;
    private final int pageSize;

    private void redrawMenu() {
        vt.cursorHome(upperCorner);
        vt.eraseDown();

        if (model.getSearchTerm().isEmpty()) {
            vt.out.println("Type to search");
            searchCursor = upperCorner;
        }
        else {
            vt.out.print(model.getSearchTerm());
            searchCursor = new Position(upperCorner.row, upperCorner.column + model.getSearchTerm().length());
            vt.out.println();
        }

        vt.out.print("--------------");

        for (int i = 0; i < model.size() && (i + upperCorner.row + 2) <= vt.screenSize.row; i++) {
            int index = i + offset;

            vt.out.println();
            if (model.getSelected() == index) {
                vt.out.print("[*] ");
            }
            else {
                vt.out.print("[ ] ");
            }

            vt.out.print(model.get(index).label);

            if (model.get(index).hasSubmenu) {
                vt.out.print(" >");
            }

        }
        vt.scrollScreen(upperCorner.row + 2, vt.screenSize.row);
        vt.cursorHome(searchCursor);
    }

    private void redrawSearch() {
        vt.cursorHome();

    }

    private void redrawMenuItem(int i) {
        if ((i - offset) >= 0 && (i - offset) <= pageSize) {
            vt.cursorHome(upperCorner.row + (i - offset) + 2, 1);

            if (model.getSelected() == i) {
                vt.out.print("[*] ");
            }
            else {
                vt.out.print("[ ] ");
            }

            vt.out.print(model.get(i).label);

            if (model.get(i).hasSubmenu) {
                vt.out.print(" >");
            }

        }
    }

    public CMenuView(CMenu model, Position upperCorner, VT vt) {
        this(model, vt);
        this.upperCorner = upperCorner;
    }

    public CMenuView(CMenu model, VT vt) {
        this.model = model;
        this.vt = vt;
        model.addCMenuChangeListener(this);
        previouslySelected = model.getSelected();
        upperCorner = new Position(1, 1);
        offset = 0;
        pageSize = (vt.screenSize.row - upperCorner.row) - 3;
    }

    public void redraw() {
        redrawMenu();
    }

    @Override
    public void CMenuItemAdded(CMenuItem c) {

    }

    @Override
    public void SelectionChanged(int selected) {

        // TODO: Im not convinced these are working...
        while ((selected - offset) < 0) {
            offset--;
            vt.scrollUp();
            redrawMenu();
        }

        while ((selected - offset) > pageSize) {
            offset++;
            vt.scrollDown();
            redrawMenu();
        }

        if (model.size() > 0) {
            redrawMenuItem(previouslySelected);
            redrawMenuItem(selected);
            previouslySelected = selected;
        } else {
            redrawMenu();
        }


        vt.cursorHome(searchCursor);
    }

    @Override
    public void CMenuItemSelected(CMenuItem c, boolean keepMenuOpen) {

    }

    @Override
    public void CMenuSearchTermChanged(String term) {
        redrawMenu();
    }

    @Override
    public boolean CMenuGoBack() {
        return false;
    }
}
