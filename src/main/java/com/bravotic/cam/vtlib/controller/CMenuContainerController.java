package com.bravotic.cam.vtlib.controller;

import com.bravotic.cam.model.CMenu;
import com.bravotic.cam.model.CMenuContainer;
import com.bravotic.cam.model.CMenuContainerListener;
import com.bravotic.vtlib.VT;
import com.bravotic.vtlib.VTController;

public class CMenuContainerController extends VTController implements CMenuContainerListener {

    private final CMenuController controller;
    private final CMenuContainer model;

    public void processInput(String input) {
        controller.processInput(input);
    }

    public CMenuContainerController(CMenuContainer model, VT vt) {
        super(vt);
        this.model = model;
        model.addListener(this);

        controller = new CMenuController(model.getTopMenu(), vt);
    }

    @Override
    public void submenuOpened(String label, CMenu menu) {
        controller.setModel(menu);
    }

    @Override
    public void submenuClosed(CMenu menu) {
        controller.setModel(model.getTopMenu());
    }

    @Override
    public void menuWouldLikeToClose() {

    }
}
