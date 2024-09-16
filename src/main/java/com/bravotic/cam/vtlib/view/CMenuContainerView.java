package com.bravotic.cam.vtlib.view;

import com.bravotic.cam.model.*;
import com.bravotic.cam.vtlib.controller.CMenuController;
import com.bravotic.vtlib.Position;
import com.bravotic.vtlib.VT;

import java.util.Stack;

public class CMenuContainerView implements CMenuContainerListener {
    private final Stack<CMenuView> views;

    private final CMenuContainer model;

    private final VT vt;

    private final Position menuCorner;
    private final StringBuilder path;

    public void redraw() {
        vt.cursorHome();
        vt.setAttributeMode(37, 44);
        // TODO: Make sure we substring this so it fits on the screen
        vt.out.print(path.toString());
        vt.eraseEndOfLine();
        vt.setAttributeMode(0);
        views.peek().redraw();
    }

    public CMenuContainerView(CMenuContainer model, VT vt) {
        views = new Stack<>();
        this.model = model;
        this.vt = vt;

        this.model.addListener(this);
        menuCorner = new Position(2, 1);

        CMenu root = model.getTopMenu();
        views.add(new CMenuView(root, menuCorner, vt));
        path = new StringBuilder();
    }

    @Override
    public void submenuOpened(String label, CMenu menu) {
        path.append("[" + label + "]");
        CMenuView view = new CMenuView(menu, menuCorner, vt);
        views.push(view);
        redraw();
    }

    @Override
    public void submenuClosed(CMenu menu) {
        CMenuView dispose = this.views.pop();
        path.delete(path.lastIndexOf("["), path.length());
        redraw();
    }

    @Override
    public void menuWouldLikeToClose() {

    }
}
