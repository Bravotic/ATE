package com.bravotic.ate.model;

/**
 * Allows a class to be notified of changes on an APrompt.
 */
public interface APromptChangeListener {
    /**
     * Invoked when the text response of a promtp is changed. This is only invoked from a text prompt.
     * @param text The text that was input.
     */
    void promptTextChanged(String text);

    /**
     * Invoked when the selected button from a button prompt is changed. This is only invoked from a button prompt.
     * @param selected The button that is selected.
     */
    void promptSelectionChanged(int selected);

    /**
     * Invoked when the prompt is submitted.
     */
    void promptSubmitted();
}
