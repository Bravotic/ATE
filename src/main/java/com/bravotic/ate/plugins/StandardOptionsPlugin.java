package com.bravotic.ate.plugins;

import com.bravotic.ate.model.*;
import com.bravotic.ate.plugins.standardoptions.StandardFileOptions;
import com.bravotic.ate.plugins.standardoptions.StandardViewOptions;
import com.bravotic.cam.model.CMenuItem;

public class StandardOptionsPlugin {
    public StandardOptionsPlugin(AState state) {
        new StandardFileOptions(state);
        new StandardViewOptions(state);

        // TODO: Experimental code, move to its own class sometime
        state.addMenuOption("View\\Guides", new CMenuItem("Add Guide", item -> {
            state.showPrompt(new APrompt("Please enter column for guide", APrompt.QUESTION, null, new TextPromptAction() {

                @Override
                public void submitted(String text) {
                    int col = Integer.parseInt(text);
                    state.getDocument().addGuide(col);

                    // TODO: Remove menu option to remove guide when guide is removed
                    state.addMenuOption("View\\Guides", new CMenuItem("Remove guide " + col, item -> {
                        state.getDocument().removeGuide(col);
                        return true;
                    }));
                }

                @Override
                public void closed() {

                }
            }));
            return true;
        }));

        state.addMenuOption("Edit", new CMenuItem("Go to line", item -> {
            ADocument doc = state.getDocument();
            state.showPrompt(new APrompt("Enter line number: ", APrompt.QUESTION, null, new TextPromptAction() {
                @Override
                public void submitted(String text) {
                    doc.move(0, Integer.parseInt(text) - 1);
                    state.setView(AState.VIEW_DOCUMENT);
                }

                @Override
                public void closed() {
                    state.setView(AState.VIEW_MENU);
                }
            }));
            return true;
        }));
    }
}
