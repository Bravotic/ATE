package com.bravotic.ate.vtlib.controller;

import com.bravotic.ate.model.APrompt;
import com.bravotic.ate.model.AState;
import com.bravotic.ate.model.AStateChangeListener;
import com.bravotic.cam.vtlib.controller.CMenuContainerController;
import com.bravotic.cam.vtlib.controller.CMenuController;
import com.bravotic.vtlib.VT;
import com.bravotic.vtlib.VTController;

/**
 * A controller for an AStateView VT View.
 */
public class AStateController extends VTController implements AStateChangeListener {
    private final AState model;
    private final VTController[] controllers;
    private VTController active;


    @Override
    public void processInput(String input) {
        // Intercept just the ESC key to flip our view
        if (input.equals("\033")) {
            // If we are in the prompt, hitting ESC makes us process the close() request instead of automatically
            // flipping the screen. Maybe check for this another way
            if (model.isPromptOpen()) {
                active.processInput("\033");
            }

            // Otherwise we flip the screen in any other view
            else {
                model.flip();
            }
        }
        // Otherwise we send our key to the other controller
        else {
            active.processInput(input);
        }
    }


    /**
     * Constructs a new controller for the model.
     * @param model The model to be controlled.
     * @param vt The VT to draw to.
     */
    public AStateController(AState model, VT vt) {
        super(vt);
        this.model = model;

        model.addListener(this);

        controllers = new VTController[3];
        controllers[AState.VIEW_MENU] = new CMenuContainerController(model.getMenuContainer(), vt);
        controllers[AState.VIEW_DOCUMENT] = new ADocumentController(model.getDocument(), vt);
        controllers[AState.VIEW_PROMPT] = new APromptController(vt);

        active = controllers[model.getActiveView()];

    }


    @Override
    public void newFile() {

    }

    @Override
    public void fileOpened(String filename) {

    }

    @Override
    public void viewIsFlipped(int viewMode) {
        active = controllers[viewMode];
    }

    @Override
    public void promptOpened(APrompt p) {
        active = controllers[AState.VIEW_PROMPT];
        ((APromptController)active).setModel(p);
    }

    @Override
    public void promptClosed() {
        if (!model.isPromptOpen()) {
            active = controllers[model.getActiveView()];
        }
    }
}

