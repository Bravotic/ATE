package com.bravotic.ate.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represents a prompt to the user. This can either be a text entry prompt, in which the user must enter their own
 * response, or a button prompt, in which responses are provided. Prompts are used to ask a user for input for an
 * operation.
 */
public class APrompt {
    /**
     * The prompt conveys info.
     */
    public static final int INFO = 0;

    /**
     * The prompt presents a question.
     */
    public static final int QUESTION = 1;

    /**
     * The prompt presents a warning.
     */
    public static final int WARNING = 2;

    /**
     * The prompt presents an error.
     */
    public static final int ERROR = 3;

    /**
     * The action to be taken when a prompt is submitted or closed.
     */
    protected TextPromptAction action;

    /**
     * The message to be shown when the prompt is shown.
     */
    protected String message;

    private final String[] options;
    private List<String> activeOptions;

    private StringBuilder promptText;
    private int selected;

    /**
     * The list of listener that are waiting for this prompt.
     */
    protected List<APromptChangeListener> listeners;

    private final int type;

    /**
     * Used to notify that the prompt typed text has changed.
     */
    protected void notifyPromptTextChanged() {
        String promptTextValue = promptText.toString();

        for (APromptChangeListener l : listeners) {
            l.promptTextChanged(promptTextValue);
        }
    }

    /**
     * Used to notify that the prompt has been submitted.
     */
    protected void notifySubmitted() {
        for (APromptChangeListener l : listeners) {
            l.promptSubmitted();
        }
    }

    /**
     * Used to notify that the selection of buttons in the prompt has changed.
     */
    protected void notifySelectionChanged() {
        for (APromptChangeListener l : listeners) {
            l.promptSelectionChanged(selected);
        }
    }

    /**
     * Constructs a new prompt. This constructor is used for both button prompts and text entry prompts.
     * @param message The message to be shown to the user.
     * @param type The type of prompt to show. Either INFO, QUESTION, WARNING, or ERROR.
     * @param options The options to be shown to the user. Use null if you wish to use a text entry.
     * @param action The action to be done once the prompt is submitted or closed.
     */
    public APrompt(String message, int type, String[] options, TextPromptAction action) {
        this.message = message;
        this.action = action;
        this.type = type;

        promptText = new StringBuilder();
        listeners = new ArrayList<>();
        this.options = options;

        selected = 0;

        if (options != null) {
            this.activeOptions = new ArrayList<>();
            activeOptions.addAll(Arrays.asList(options));
        }
    }

    /**
     * Add a change listener to the APrompt.
     * @param l The listener to add.
     */
    public void addChangeListener(APromptChangeListener l) {
        listeners.add(l);
    }

    /**
     * Append a character to the end of the prompt's text field. This only has an effect when a text prompt is used.
     * @param ch The character to append.
     */
    public void append(char ch) {
        promptText.append(ch);
        notifyPromptTextChanged();
    }

    /**
     * Delete the last character at the end of the prompt's text field. This only has an effect when a text prompt is
     * used.
     */
    public void delete() {
        if (promptText.length() > 0) {
            promptText.deleteCharAt(promptText.length() - 1);
            notifyPromptTextChanged();
        }
    }

    /**
     * Submit the prompt. This will call the submit action from the TextPromptAction.
     */
    public void submit() {
        String submitString;

        if (options == null) {
            submitString = promptText.toString();
        }
        else {
            submitString = activeOptions.get(selected);
        }

        action.submitted(submitString);
        notifySubmitted();

        // TODO: Maybe not imply this?
        listeners.clear();
        promptText = new StringBuilder();
    }

    /**
     * Close the prompt. This will invoke the close method on the TextPromptAction.
     */
    public void close() {
        action.closed();
        notifySubmitted();
    }

    /**
     * Gets the selected button. This only has effect when a button prompt is used.
     * @return The index of the button selected.
     */
    public int getSelected() {
        return selected;
    }

    /**
     * Gets the type of the prompt.
     * @return The type of the prompt.
     */
    public int getType() {
        return type;
    }

    /**
     * Select the next option in a button prompt. Note that this only has an effect in a button prompt.
     */
    public void next() {
        if (options != null) {
            selected = Math.min(selected + 1, options.length - 1);
            notifySelectionChanged();
        }
    }

    /**
     * Select the last item in a button prompt. Note that this only has an effect in a button prompt.
     */
    public void last() {
        if (options != null) {
            selected = Math.max(selected - 1, 0);
            notifySelectionChanged();
        }
    }

    /**
     * Gets the message that is prompt to the user.
     * @return The message that is prompted to the user.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Gets the options that the user can select from.
     * @return An array of possible options, or null if the prompt is a text prompt.
     */
    public String[] getOptions() {
        if (options != null) return activeOptions.toArray(new String[0]);
        else return null;
    }

    /**
     * Gets the text of the text box. Note that this option only works in a text prompt.
     * @return The text of the text box.
     */
    public String getPromptText() {
        return promptText.toString();
    }
}
