package com.bravotic.ate.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.awt.Point;

/**
 * Represents a document in the Advanced Text Editor. Within this document, text can be inserted and removed, lines can
 * be added or removed, and the cursor can be moved to any line or column within it. It also handles scrolling and
 * screen space coordinates granted a lineHeight and charWidth are set. While this handling of screen space coordinates
 * may better be handled by a view, this allows us to account for cursor movement, for example moving a cursor up from
 * one line to another.
 * In general, this class only represents the actual editing of the file, if you want the loading or saving of a file,
 * see AState.
 *
 * @see com.bravotic.ate.model.AState
 */
public class ADocument {
    private final List<StringBuilder> lines;
    private final List<Integer> guides;

    private StringBuilder currentLine;

    private int col;
    private int line;

    /**
     * In this codebase, we keep two separate accounts of where the cursor is. First is the traditional col/line system
     * which we use to edit the text, the second is the screen cursor which we use to properly position the cursor on
     * screen. This is used because some letters are not the same size, which makes it hard to determine where the
     * screen cursor should be based solely off col/line measurements.
     */
    private Point screenCursor;

    private final List<ADocumentChangeListener> listeners;


    // TODO: Maybe include this in a special ADocumentLine class
    /**
     * A measure of how many PIXELS make up each page. This may be substituted for LINES if the view has no concept of
     * pixels. If it is substituted for LINES, the value MUST be 1.
     */
    private int lineHeight;

    // TODO: This won't work for variable width fonts, maybe eventually include some FontMetrics object here
    /**
     * How many PIXELS make up each character in width. NOTE that this could be substituted for 1 if the view has no
     * concept of pixels.
     */
    private int charWidth;


    /**
     * How many LINES make up the page. If your view uses PIXELS instead of LINES, divide it by LINE HEIGHT.
     */
    private int linesPerPage;

    /**
     * How many pixels we are scrolled in the X direction. This can be substituted for characters if char width is 1.
     */
    private int scrollX;

    /**
     * How many pixels we are scrolled in the Y direction. This can be substituted for characters if lineHeight is 1.
     */
    private int scrollY;

    private int tabStop;

    private void setCursorFromXY(int x, int y) {
        int clickCol, clickLine;

        // Since our lines all have to be the same height, we can simply assume y/lineHeight gives us the line we are
        // on.
        clickLine = (int)Math.floor((double)y / lineHeight);

        // Wrap the lines to our document, if we are less than zero, we wrap to zero, and likewise if we are greater
        // than size, wrap to size.
        if (clickLine < 0) {
            clickLine = 0;
        }
        else  if (clickLine >= lines.size()){
            clickLine = lines.size() - 1;
        }

        currentLine = lines.get(clickLine);

        int penX = 0;
        int tabWidthSize = charWidth * tabStop;

        // Begin tracing through the current selected line to find the optimal x position for the click. We need to do
        // this since unlike lines, text characters do not have a predetermined width.
        for (clickCol = 0; clickCol < currentLine.length() && penX < x; clickCol++) {
            int increment;

            if (currentLine.charAt(clickCol) == '\t') {
                increment = tabWidthSize - (penX % tabWidthSize);
            }
            else {
                increment = charWidth;
            }

            if (penX + increment > x) {
                if (x - penX > increment/2) {
                    penX += increment;
                    clickCol++;
                }

                break;
            }
            else {
                penX += increment;
            }
        }

        this.col = clickCol;
        this.line = clickLine;

        screenCursor.x = penX;
        screenCursor.y = clickLine * lineHeight;
    }

    private void setCursorFromColLine(int col, int line) {
        this.line = Math.min(line, lines.size() - 1);
        currentLine = lines.get(this.line);
        this.col = Math.min(col, currentLine.length());

        screenCursor.y = this.line * lineHeight;

        int penX = 0;
        int tabWidthSize = charWidth * tabStop;

        for (int i = 0; i < this.col; i++) {
            char ch = currentLine.charAt(i);
            if (ch == '\t') {
                penX += tabWidthSize - (penX % tabWidthSize);
            }
            else {
                penX += charWidth;
            }
        }
        screenCursor.x = penX;
    }

    /**
     * Represents which direction a cursor can move.
     */
    public enum CursorDirection {
        /**
         * Cursor moves left.
         */
        CursorLeft,

        /**
         * Cursor moves right.
         */
        CursorRight,

        /**
         * Cursor moves up.
         */
        CursorUp,

        /**
         * Cursor moves down.
         */
        CursorDown
    }

    /**
     * Construct a new document model with initial content.
     * @param content The initial content.
     */
    public ADocument(String content) {
        lines = new ArrayList<>();
        guides = new ArrayList<>();
        listeners = new ArrayList<>();

        // TODO: Transition these to a Cursor class
        col = 0;
        line = 0;
        screenCursor = new Point(0, 0);

        tabStop = 8;

        for (String line : content.split("\n")) {
            lines.add(new StringBuilder(line));
        }

        currentLine = lines.get(line);

        scrollY = 0;
        lineHeight = 1;
        charWidth = 1;
    }

    /**
     * Constructs a document from a list of lines
     * @param lines Lines to be loaded
     * @deprecated
     */
    public ADocument(List<StringBuilder> lines) {
        this.lines = lines;
        guides = new ArrayList<>();
        listeners = new ArrayList<>();
        col = 0;
        line = 0;
        currentLine = lines.get(line);

        lineHeight = 0;
        charWidth = 0;
        scrollY = 0;
    }

    /**
     * Construct a new document model with no initial content.
     */
    public ADocument() {
        this("");
    }

    /**
     * Add a change listener which is notified when changes happen to the document.
     * @param l The listener to add.
     */
    public void addChangeListener(ADocumentChangeListener l) {
        listeners.add(l);
    }

    // Cursor stuff
    // =================================================================================================================

    /**
     * Checks to see if the cursor is outside the viewable area, and scrolls the screen to make sure the cursor is
     * within it.
     */
    private void scrollCursorIntoView() {
        // TODO: This is only for Y scrolling, make this work for X scrolling too
        int firstLineVisible = (int)Math.floor((double)scrollY / (double)lineHeight);
        int lastLineVisible = Math.min(firstLineVisible + linesPerPage, this.lines.size() - 1);
        int lastScrollY = scrollY;

        if (line - 1 < firstLineVisible) {
            scrollY = Math.max((line - 1) * lineHeight, 0);

            for (ADocumentChangeListener l : listeners) {
                l.documentScrolled(0, scrollY - lastScrollY);
            }
        }
        else if (line + 1 > lastLineVisible && line + 1 < lines.size()) {
            scrollY = ((line + 1) - linesPerPage) * lineHeight;

            for (ADocumentChangeListener l : listeners) {
                l.documentScrolled(0, scrollY - lastScrollY);
            }
        }

        // TODO: see why we can't just implement this into the previous function
        else if (line > lastLineVisible) {
            scrollY = (line - linesPerPage) * lineHeight;

            for (ADocumentChangeListener l : listeners) {
                l.documentScrolled(0, scrollY - lastScrollY);
            }
        }


    }

    /**
     * Move the cursor to the position specified.
     * @param col The column the cursor should move to.
     * @param line The line the cursor should move to.
     */
    public void move(int col, int line) {
        if (this.lines.isEmpty()) {
            lines.add(new StringBuilder());
        }

        setCursorFromColLine(col, line);
        scrollCursorIntoView();

        for(ADocumentChangeListener l : listeners) {
            l.positionChanged(this.col, this.line);
        }
    }

    /**
     * Move the cursor in a specific direction.
     * @param dir The direction to move the cursor in
     * @see com.bravotic.ate.model.ADocument.CursorDirection
     */
    public void cursorMove(CursorDirection dir) {
        switch(dir) {
            case CursorUp:
                if (line > 0) {
                    setCursorFromXY(screenCursor.x, screenCursor.y - lineHeight);
                }
                break;
            case CursorDown:
                if (line < lines.size() - 1) {
                    setCursorFromXY(screenCursor.x, screenCursor.y + lineHeight);
                }
                break;
            case CursorLeft:
                if (col > 0) {
                    setCursorFromColLine(col - 1, line);
                }
                break;
            case CursorRight:
                if (col < currentLine.length()) {
                    setCursorFromColLine(col + 1, line);
                }
                break;
        }

        scrollCursorIntoView();

        for(ADocumentChangeListener l : listeners) {
            l.positionChanged(col, line);
        }
    }

    /**
     * Move the cursor to a position where there was a click on screen. Note that this automatically considers the
     * scroll. The x and y coordinates given SHOULD NOT be translated by the scroll.
     * @param x The x position of the click.
     * @param y The y position of the click.
     */
    public void clickAt(int x, int y) {
        setCursorFromXY(x + scrollX, y + scrollY);

        for (ADocumentChangeListener l : listeners) {
            l.positionChanged(col, line);
        }
    }

    /**
     * Move the cursor one full page down. This will cause the screen to scroll to show it.
     */
    public void pageDown() {
        // If our page is less than the maximum amount of lines that can fit on screen, we just jump to the last line.
        if (lines.size() <= line + linesPerPage) {
            line = lines.size() - 1;
        }
        // Otherwise we scroll down and move down a page
        else {
            scrollY += lineHeight * linesPerPage;

            // TODO: Maybe a better way of doing this?
            if (line == 0) {
                scrollY -= lineHeight;
            }

            line = Math.min(line + linesPerPage, lines.size());
        }

        currentLine = lines.get(line);

        for(ADocumentChangeListener l : listeners) {
            // TODO: replace the deltas with positions.
            l.documentScrolled(0, 0);
            l.positionChanged(col, line);
        }
    }

    /**
     * Scroll the screen up one entire page.
     */
    public void pageUp() {
        // If our page is less than the maximum amount of lines that can fit on screen, we just jump to the last line.
        if (line - linesPerPage < 0) {
            line = 0;
            scrollY = 0;
        }
        // Otherwise we scroll down and move down a page
        else {
            scrollY -= lineHeight * linesPerPage;

            line = Math.max(line - linesPerPage, 0);

            // TODO: Maybe a better way of doing this?
            if (scrollY < 0) {
                scrollY = 0;
            }
        }

        currentLine = lines.get(line);

        for(ADocumentChangeListener l : listeners) {
            // TODO: replace the deltas with positions.
            l.documentScrolled(0, 0);
            l.positionChanged(col, line);
        }
    }


    /**
     * Gets the current column, or X position of the cursor.
     * Note: This does NOT report the screenspace position, just the raw position in the text buffer.
     * @return The current column position of the cursor.
     * @see getScreenCursorX
     */
    public int getCursorX() {
        return this.col;
    }

    /**
     * Gets the current row, or Y position of the cursor.
     * @return The current row position of the cursor.
     */
    public int getCursorY() {
        return this.line;
    }

    /**
     * Gets the current X position of the cursor as drawn on the screen. This is done by taking character width into
     * perspective.
     * @return The X coordinate of the cursor.
     */
    public int getScreenCursorX() {
        return screenCursor.x;
    }

    /**
     * Gets the current Y position of the cursor as drawn on the screen.
     * @return The Y coordinate of the cursor.
     */
    public int getScreenCursorY() {
        return screenCursor.y;
    }

    // Document Manipulation
    //==================================================================================================================


    /**
     * Private function to do the actual insertion into the line without calling any notifiers
     * @param ch Character to insert
     */
    private void insertInner(char ch) {
        if (ch == '\n') {
            // First grab everything right of where we hit enter, as this will compose our new line.
            String forNewLine = currentLine.substring(col);

            // Now remove that from the current line
            currentLine.delete(col, currentLine.length());

            // Move to the next line and reset ourselves to column 0
            line++;
            col = 0;

            // Update our screen cursor as well
            screenCursor.y += lineHeight;
            screenCursor.x = 0;

            // Add the new line with our split data
            lines.add(line, new StringBuilder(forNewLine));

            // Update
            currentLine = lines.get(line);
        }
        else {
            currentLine.insert(col, ch);
            col++;

            // Update our screen space coords
            // If we have encountered a tab, we need to calculate its variable width
            if (ch == '\t') {
                int tabWidthSize = charWidth * tabStop;
                screenCursor.x += tabWidthSize - (screenCursor.x % tabWidthSize);
            }
            // Otherwise just add a char width to it.
            else {
                screenCursor.x += charWidth;
            }
        }
    }

    /**
     * Insert the specified character at the current cursor position.
     * @param ch The character to insert.
     */
    public void insert(char ch) {
        insertInner(ch);

        if (ch == '\n') {
            scrollCursorIntoView();

            for(ADocumentChangeListener l : listeners) {
                l.lineInserted(line);
                l.positionChanged(col, line);
            }
        }
        else {
            for(ADocumentChangeListener l : listeners) {
                l.textInserted(col, line, ch);
                l.positionChanged(col, line);
            }
        }
    }

    /**
     * Insert a string at the current cursor position.
     * @param text The text to insert.
     */
    public void insert(String text) {
        for (char c : text.toCharArray()) {
            insert(c);
        }
    }

    void bufferInsert(String text) {
        for (char c : text.toCharArray()) {
            insertInner(c);
        }

        for (ADocumentChangeListener l : listeners) {
            l.documentContentsReplaced();
        }
    }

    /**
     * Delete a character from the current cursor position. This is the action of hitting backspace in the buffer.
     */
    public void remove() {
        // If we are not at the start of the line, we delete the char previous to us.
        if (col > 0) {
            currentLine.deleteCharAt(col - 1);
            col--;

            // TODO: Actually implement the logic for this instead of using this cheap workaround
            setCursorFromColLine(col, line);

        }

        // If we are not at the first line, we delete the current line and append it to the last line.
        else if (line != 0) {
            StringBuilder newCurrentLine = lines.get(line - 1);

            col = newCurrentLine.length();
            line--;

            newCurrentLine.append(currentLine.toString());
            lines.remove(currentLine);

            currentLine = newCurrentLine;

            // TODO: Implement logic here instead of this function call
            setCursorFromColLine(col, line);

            scrollCursorIntoView();

            for(ADocumentChangeListener l : listeners) {
                l.lineRemoved(line + 1);
                l.positionChanged(col, line);
            }

            return;
        }

        // Otherwise we return, since we can't do anything...
        else {
            return;
        }

        // If we were able to do something, notify our listeners that our text has changed, and our position as well.
        for(ADocumentChangeListener l : listeners) {
            l.textRemoved(col, line);
            l.positionChanged(col, line);
        }

    }

    /**
     * Adds a vertical guide at the specified column.
     * @param col The column to add the guide at.
     */
    public void addGuide(int col) {
        guides.add(col);
    }

    /**
     * Removes a guide from the specified column.
     * NOTE: If multiple instances of the guide were added, this might not remove them all.
     * @param col The column to remove the guide from.
     */
    public void removeGuide(int col) {
        guides.remove((Integer)col);
    }

    /**
     * Get a list of guides that the document has.
     * @return A list of guides.
     */
    public List<Integer> getGuides() {
        return Collections.unmodifiableList(guides);
    }

    // Internal buffer stuff
    // =================================================================================================================

    /**
     * Clears the contents of the buffer. Intended to be used when buffer content is replaced.
     */
    public void bufferClear() {
        lines.clear();
        lines.add(new StringBuilder());

        line = 0;
        col = 0;

        currentLine = lines.get(0);
    }

    /**
     * Checks whether the buffer is empty.
     * @return true if the buffer is empty, false otherwise.
     */
    public boolean isEmpty() {
        return lines.size() == 1 && lines.get(0).length() == 0;
    }

    /**
     * Gets the string contents of the requested line.
     * @param line The line to get the contents of.
     * @return The String value of the line.
     */
    public String getLine(int line) {
        return lines.get(line).toString();
    }

    /**
     * Gets the total line count for the buffer.
     * @return The amount of lines in the buffer.
     */
    public int getLineCount() {
        return lines.size();
    }

    /**
     * Turns the whole buffer into a String. Inserts System.lineSeparator on new lines.
     * @return The String contents of the entire buffer.
     */
    public String toString() {
        StringBuilder doc = new StringBuilder();
        String sep = System.lineSeparator();

        // Go through every line and add it, except for the last line. We will add that one manually without a line
        // break after it.
        for (int i = 0; i < lines.size() - 1; i++) {
            doc.append(lines.get(i).toString());
            doc.append(sep);
        }

        doc.append(lines.get(lines.size() - 1));

        return doc.toString();
    }

    // Sizing stuff here
    // =================================================================================================================

    /**
     * Sets the amount of lines there are per page. TODO: Evaluate if this is still needed given that we now also work
     * in screen coordinates and sizes.
     * @param linesPerPage The amount of lines per page.
     */
    public void setLinesPerPage(int linesPerPage) {
        this.linesPerPage = linesPerPage;
    }

    /**
     * Sets how large each line is.
     * @param lineHeight The height of every line.
     */
    public void setLineHeight(int lineHeight) {
        this.lineHeight = lineHeight;
    }

    /**
     * Gets the height of each line.
     * @return The height of each line.
     */
    public int getLineHeight() {
        return lineHeight;
    }

    /**
     * Sets the width of each character. This assumes each and every character is monospaced, meaning they all have the
     * same width. A monospace font must be used.
     * @param charWidth The width of every char.
     */
    public void setCharWidth(int charWidth) {
        this.charWidth = charWidth;
    }

    /**
     * Get the width of every char.
     * @return The width of every char.
     */
    public int getCharWidth() {
        return charWidth;
    }

    /**
     * Gets the height of each page.
     * @return The height of each page.
     */
    public int getPageHeight() {
        return this.lineHeight * this.lines.size();
    }

    /**
     * Sets how many characters max the TAB character is rendered as. Defaults to 8, but can be set to anything that is
     * non-zero.
     * @param tabStop The max size (in characters) of the TAB character
     */
    public void setTabStop(int tabStop) {
        if (tabStop < 1) {
            throw new IllegalArgumentException("Tab stop cannot be less than zero");
        }

        this.tabStop = tabStop;

        // Since we changed the tab size, we need to update our screen space cursor coordinates. It may have moved.
        setCursorFromColLine(col, line);

        // Notify our views to update with the new tabstop size
        for (ADocumentChangeListener l : listeners) {
            l.tabStopChanged(this.tabStop);
        }
    }

    /**
     * Gets the tabstop, this is how many spaces should be added when the TAB character is pressed. This defaults to
     * 8.
     * @return The maximum amount of spaces added when TAB is pressed.
     */
    public int getTabStop() {
        return tabStop;
    }


    // Scrolling stuff here
    // =================================================================================================================

    /**
     * Scroll the page down or up.
     * @param amount The amount to scroll, positive is up, negative is down.
     */
    public void scrollY(int amount) {
        int delta;

        // TODO: Turn lines.size() * lineHeight into a variable
        // TODO: Fix bug where scroll wraps around.
        if (amount + scrollY >= 0 && amount + scrollY <= (lines.size() * lineHeight)) {
            scrollY += amount;
            delta = amount;
        }
        else {
            scrollY = 0;
            delta = 0;
        }

        for (ADocumentChangeListener l : listeners) {
            l.documentScrolled(0, delta);
        }
    }

    /**
     * Scroll the page left or right.
     * @param amount The amount to scroll, positive is right, negative is left.
     */
    public void scrollX(int amount) {
        scrollX += amount;
    }

    /**
     * How many lines scrolled down are we.
     * @return The lines from the top we are scrolled down.
     */
    public int getScrollY() {
        return scrollY;
    }

    /**
     * How many characters from the left are we scrolled over.
     * @return The amount of characters from the left we are scrolled over.
     */
    public int getScrollX() {
        return scrollX;
    }
}
