package com.bravotic.ate.vtlib.view;

import com.bravotic.ate.model.APromptChangeListener;
import com.bravotic.ate.model.APrompt;
import com.bravotic.vtlib.VT;

/**
 * Draws an APrompt to a VT view.
 */
public class APromptView implements APromptChangeListener {
    private APrompt model;

    private final VT vt;

    /**
     * Constructs a new view given a model.
     * @param model The model to display.
     * @param vt The VT to draw to.
     */
    public APromptView(APrompt model, VT vt) {
        this(vt);
        setModel(model);
    }

    /**
     * Constructs a new view without a given model, the model will need to be supplied later with setModel().
     * @param vt The VT to draw to.
     */
    public APromptView(VT vt) {
        this.model = null;
        this.vt = vt;
    }

    /**
     * Sets the model to be drawn.
     * @param model The model to be shown.
     */
    public void setModel(APrompt model) {
        this.model = model;
        model.addChangeListener(this);
    }

    /**
     * Redraws the options to the VT.
     */
    protected void redrawOptions() {
        String[] options = model.getOptions();
        if (options != null) {
            int selected = model.getSelected();

            for (int i = 0; i < options.length; i++) {
                if (i == selected) {
                    vt.out.print("[" + options[i] + "]");
                }
                else {
                    vt.out.print(" " + options[i] + " ");
                }

                vt.out.print(" ");
            }
        }
        else {
            vt.out.print(model.getPromptText());
            vt.eraseEndOfLine();
        }
    }

    /**
     * Redraws the screen.
     */
    public void redraw() {
        vt.cursorHome();
        vt.eraseScreen();

        vt.setAttributeMode(37, 44);
        switch(model.getType()) {
            case APrompt.INFO:
                vt.out.print("(i) Information");
                break;
            case APrompt.QUESTION:
                vt.out.print("(?) Question");
                break;
            case APrompt.WARNING:
                vt.out.print("(!) Warning");
                break;
            case APrompt.ERROR:
                vt.out.print("(X) Error");
                break;
        }


        vt.eraseEndOfLine();
        vt.setAttributeMode(0);
        vt.out.println();

        vt.out.println(model.getMessage());


        redrawOptions();
    }

    @Override
    public void promptTextChanged(String text) {
        vt.cursorHome(3, 1);
        redrawOptions();
    }

    @Override
    public void promptSelectionChanged(int selected) {
        vt.cursorHome(3, 1);
        redrawOptions();
    }

    @Override
    public void promptSubmitted() {

    }
}
