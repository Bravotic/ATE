package com.bravotic.ate.vtlib.view;

import com.bravotic.ate.model.ADocument;
import com.bravotic.ate.model.APrompt;
import com.bravotic.ate.model.AState;
import com.bravotic.ate.model.AStateChangeListener;
import com.bravotic.cam.model.CMenuContainer;
import com.bravotic.cam.vtlib.view.CMenuContainerView;
import com.bravotic.vtlib.VT;

/**
 * Allows the AState to be drawn to the VT. Indirectly manages the document, prompt, and menu views.
 */
public class AStateView implements AStateChangeListener  {
    private final CMenuContainerView menuView;
    private final ADocumentView docView;

    private final APromptView promptView;
    private final VT vt;
    private final AState model;

    private void switchToView(int view) {
        vt.cursorHome();

        switch(view) {
            case AState.VIEW_DOCUMENT:
                docView.redraw();
                break;
            case AState.VIEW_MENU:
                menuView.redraw();
                break;
            case AState.VIEW_PROMPT:
                promptView.redraw();
                break;
        }
    }

    /**
     * Construct a new view with a given state.
     * @param a The state to be drawn to the VT.
     * @param vt The VT to draw to.
     */
    public AStateView(AState a, VT vt) {
        this.model = a;
        CMenuContainer container = a.getMenuContainer();
        menuView = new CMenuContainerView(container, vt);

        ADocument doc = a.getDocument();
        docView = new ADocumentView(doc, model, vt);

        promptView = new APromptView(vt);

        this.vt = vt;

        a.addListener(this);

        if (a.isMenuOpen()) {
            menuView.redraw();
        }
        else {
            docView.redraw();
        }
    }

    @Override
    public void newFile() {

    }

    @Override
    public void fileOpened(String filename) {

    }

    @Override
    public void viewIsFlipped(int viewMode) {
        switchToView(viewMode);
    }

    @Override
    public void promptOpened(APrompt p) {
        promptView.setModel(p);
        switchToView(AState.VIEW_PROMPT);
    }

    @Override
    public void promptClosed() {
        if (!model.isPromptOpen()) {
            switchToView(model.getActiveView());
        }
    }
}
