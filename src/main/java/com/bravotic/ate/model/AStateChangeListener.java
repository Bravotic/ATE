package com.bravotic.ate.model;

/**
 * Allows a class to be notified of changes on an AState.
 */
public interface AStateChangeListener {

    /**
     * Invoked when a new file buffer is created.
     */
    void newFile();

    /**
     * Invoked when a file is opened.
     * @param filename The name of the file that was opened.
     */
    void fileOpened(String filename);

    /**
     * Invoked when the view is switched.
     * @param viewMode The view that is now presented on the window.
     */
    void viewIsFlipped(int viewMode);

    /**
     * Invoked when a prompt is opened.
     * @param p The prompt that is being shown.
     */
    void promptOpened(APrompt p);

    /**
     * Invoked when a prompt has been closed.
     */
    void promptClosed();
}
