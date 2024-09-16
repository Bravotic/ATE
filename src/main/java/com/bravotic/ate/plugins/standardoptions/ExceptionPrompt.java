package com.bravotic.ate.plugins.standardoptions;

import com.bravotic.ate.model.APrompt;
import com.bravotic.ate.model.AState;
import com.bravotic.ate.model.TextPromptAction;

public class ExceptionPrompt extends APrompt implements TextPromptAction {
    private static final String[] ok = {"Ok"};
    private final AState state;

    public ExceptionPrompt(AState state, Exception e) {
        super(e.getMessage(), APrompt.ERROR, ok, null);
        this.action = this;
        this.state = state;
    }

    @Override
    public void submitted(String text) {
        state.setView(AState.VIEW_DOCUMENT);
    }

    @Override
    public void closed() {
        state.setView(AState.VIEW_DOCUMENT);
    }
}
