package com.bravotic.ate.model;

/**
 * Notification interface for ADocuments.
 */
public interface ADocumentChangeListener {

    /**
     * Called when text is inserted into a document.
     * @param col The column where the text is inserted.
     * @param line The line where the text is inserted.
     * @param ch The character which was inserted.
     */
    void textInserted(int col, int line, char ch);

    /**
     * Called when text is removed from a document.
     * @param col The column where the text was removed from.
     * @param line The line where the text was removed from.
     */
    void textRemoved(int col, int line);

    /**
     * Called when a line is inserted. This is usually done by the user hitting RETURN.
     * @param line The line which was inserted.
     */
    void lineInserted(int line);

    /**
     * Called when a line is removed.
     * @param line The line that was removed (now will point to the line which was previously below it).
     */
    void lineRemoved(int line);

    /**
     * Called when the position of the cursor is changed.
     * @param col The column position of the cursor.
     * @param line The line position of the cursor.
     */
    void positionChanged(int col, int line);

    /**
     * Called when the document is scrolled either up/down or left/right.
     * @param deltaX How far scrolled on the X axis.
     * @param deltaY How far scrolled on the Y axis.
     */
    void documentScrolled(int deltaX, int deltaY);

    /**
     * Called when the contents of the document are replaced. Usually done when opening a file or creating a new one.
     */
    void documentContentsReplaced();

    /**
     * Called when the size of a TAB is changed.
     * @param tabStop The new size of the TAB character.
     */
    void tabStopChanged(int tabStop);
}
