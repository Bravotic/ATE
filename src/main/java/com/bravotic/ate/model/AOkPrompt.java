package com.bravotic.ate.model;

/**
 * An Ok prompt. This is made to simplify creating Ok prompt boxes to show information to the user. This prompt does
 * not perform an action when submitted.
 */
public class AOkPrompt extends APrompt {
    /**
     * The options for the prompt. For this prompt, we only have the option "OK".
     */
    private static final String[] options = {"Ok"};

    /**
     * The action for the prompt, an OK prompt shouldn't do anything when submitted.
     */
    private static final TextPromptAction actions = new TextPromptAction() {
        @Override
        public void submitted(String text) {

        }

        @Override
        public void closed() {

        }
    };

    /**
     * Constructs a prompt prompting the user to say "OK".
     * @param message The message to be shown to the user.
     * @param type The type of the prompt.
     */
    public AOkPrompt(String message, int type) {
        super(message, type, options, actions);
    }
}
