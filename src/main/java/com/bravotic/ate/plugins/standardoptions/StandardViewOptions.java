package com.bravotic.ate.plugins.standardoptions;

import com.bravotic.ate.model.*;
import com.bravotic.cam.model.CMenuAction;
import com.bravotic.cam.model.CMenuItem;

public class StandardViewOptions implements CMenuAction {
    private final AState state;

    private boolean setTabSize() {
        state.showPrompt(new APrompt("Please enter tab width", APrompt.QUESTION, null, new TextPromptAction() {
            @Override
            public void submitted(String text) {
                state.getDocument().setTabStop(Integer.parseInt(text));
                state.setView(AState.VIEW_DOCUMENT);
            }

            @Override
            public void closed() {

            }
        }));
        return true;
    }

    private boolean whereAmI() {
        ADocument doc = state.getDocument();
        int col = doc.getCursorX() + 1;
        int line = doc.getCursorY() + 1;
        state.showPrompt(new AOkPrompt("Line: " + line + ", Column: " + col + ".", APrompt.INFO));
        return true;
    }

    @Override
    public boolean menuItemSelected(CMenuItem item) {
        switch (item.label) {
            case "Set Tab Size":
                return setTabSize();
            case "Where Am I":
                return whereAmI();
        }
        return false;
    }

    public StandardViewOptions(AState state) {
        this.state = state;
        state.addMenuOption("View", new CMenuItem("Set Tab Size", this));
        state.addMenuOption("View", new CMenuItem("Where Am I", this));
    }
}
