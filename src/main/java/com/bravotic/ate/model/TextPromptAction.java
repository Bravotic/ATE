package com.bravotic.ate.model;

/**
 * Actions to be taken when an APrompt is submitted or closed.
 */
public interface TextPromptAction {
    /**
     * Action to be performed when a prompt is submitted
     * @param text
     * @return the viewMode from AState to return to.
     */
    void submitted(String text);

    /**
     * Action to be taken when an APrompt is closed.
     */
    void closed();
}
