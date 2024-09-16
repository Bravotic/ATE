package com.bravotic.ate.vtlib.controller;
import com.bravotic.ate.model.APrompt;
import com.bravotic.vtlib.VT;
import com.bravotic.vtlib.VTController;

/**
 * A controller for an APromptView VT View.
 */
public class APromptController extends VTController {
    private APrompt model;

    @Override
    public void processInput(String input) {
        switch (input) {
            case "\011":
            case "\033[B":
                model.next();
                break;
            case "\033\011":
            case "\033[Z":
            case "\033[A":
                model.last();
                break;
            case "\012":
            case "\033[C":
                model.submit();
                break;
            case "\033":
            case "\033[D":
                model.close();
                break;
        }

        if (input.length() == 1) {
            char ch = input.charAt(0);

            if (ch >= ' ' && ch < 127) {
                model.append(ch);
            }
            else if (ch == 127 || ch == 8) {
                model.delete();
            }
        }
    }

    /**
     * Constructs a controller for an APrompt.
     * @param model The model to be controlled.
     * @param vt The VT to be drawn to.
     */
    public APromptController(APrompt model, VT vt) {
        this(vt);
        setModel(model);
    }

    /**
     * Constructs a controller for an APrompt that is not specified. It must be set later using the setModel method.
     * @param vt The VT to render to.
     */
    public APromptController(VT vt) {
        super(vt);
        this.model = null;
    }

    /**
     * Sets the model to be controlled.
     * @param model The model to be controlled.
     */
    public void setModel(APrompt model) {
        this.model = model;
    }
}
