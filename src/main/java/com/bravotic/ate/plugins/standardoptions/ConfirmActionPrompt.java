package com.bravotic.ate.plugins.standardoptions;

import com.bravotic.ate.model.APrompt;
import com.bravotic.ate.model.AState;
import com.bravotic.ate.model.TextPromptAction;

public class ConfirmActionPrompt extends APrompt implements TextPromptAction {

    private static final String[] options = {"No", "Yes"};
    private final Runnable ifYesAction;
    private final AState state;

    public ConfirmActionPrompt(AState state, String message, Runnable ifYesAction) {
        super(message, APrompt.WARNING, options, null);
        this.action = this;
        this.ifYesAction = ifYesAction;
        this.state = state;
    }

    @Override
    public void submitted(String text) {
        if (text.equals("Yes")) {
            ifYesAction.run();
        }
        else {
            closed();
        }
    }

    @Override
    public void closed() {
        state.setView(AState.VIEW_MENU);
    }
}
