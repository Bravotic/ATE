package com.bravotic.ate.vtlib.controller;
import com.bravotic.vtlib.VTController;

import com.bravotic.ate.model.ADocument;
import com.bravotic.vtlib.VT;

import java.util.Arrays;

/**
 * A controller for a ADocumentView VT view.
 */
public class ADocumentController extends VTController {
    private final ADocument model;

    @Override
    public void processInput(String input) {
        switch(input) {
            case "\033[A":
                model.cursorMove(ADocument.CursorDirection.CursorUp);
                break;
            case "\033[B":
                model.cursorMove(ADocument.CursorDirection.CursorDown);
                break;
            case "\033[C":
                model.cursorMove(ADocument.CursorDirection.CursorRight);
                break;
            case "\033[D":
                model.cursorMove(ADocument.CursorDirection.CursorLeft);
                break;
        }

        // TODO: I think paste is making an input of a large size, loop over our input and add it character by
        // character.
        if (input.length() == 1) {
            char ch = input.charAt(0);

            if (ch >= ' ' && ch < 127 || ch == 10 || ch == '\t') {
                model.insert(ch);
            }
            else if (ch == 127 || ch == 8) {
                model.remove();
            }
        }
    }

    /**
     * Constructs a new controller for the given document and VT.
     * @param model The model to control.
     * @param vt The VT to render to.
     */
    public ADocumentController(ADocument model, VT vt) {
        super(vt);
        this.model = model;
    }
}
