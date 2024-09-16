package com.bravotic.ate.vtlib.view;

import com.bravotic.ate.model.*;
import com.bravotic.vtlib.VT;

/**
 * Represents a view for an ADocument, drawing to a VT.
 */
public class ADocumentView implements ADocumentChangeListener, AStateChangeListener {
    private final int originX;
    private final int originY;
    private final ADocument model;

    private final VT vt;

    private int tabStop;

    private String docTitle;

    private final int coordPos;

    // TODO: Add cursor coordinates to title bar
    private void redrawTitle() {
        vt.cursorHome(1, 1);
        vt.setAttributeMode(37, 44);
        vt.out.print(docTitle);
        vt.eraseEndOfLine();
        vt.setAttributeMode(0);
        //vt.cursorHome(model.getScreenCursorY(), model.getScreenCursorX());
    }

    private void redrawGuides() {
        vt.setAttributeMode(2);
        for (int guide : model.getGuides()) {
            vt.cursorHome(2, guide + 1);
            for (int i = 0; i < vt.screenSize.row; i++) {
                vt.out.print("|");
                vt.cursorBackward();
                vt.cursorDown();
            }
        }
        vt.setAttributeMode(0);
    }

    private void redrawGuidesLine(int line) {
        vt.setAttributeMode(2);
        for (int guide : model.getGuides()) {
            vt.cursorHome(line, guide + 1);
            vt.out.print("|");
        }
        vt.setAttributeMode(0);
    }

    // TODO: Maybe remove this... not sure if its benefits outweigh the performance hit
    // TODO: RFC this versus the "Where Am I" Command.
//    private void redrawCoords(int x, int y) {
//        vt.cursorHome(1, coordPos);
//        vt.setAttributeMode(37, 44);
//        vt.out.print(x + "," + y);
//        vt.eraseEndOfLine();
//        vt.setAttributeMode(0);
//    }

    private int drawTabStop(int col) {
        int tabSize = tabStop - (col % tabStop);
        int i;

        for (i = 0; i < tabSize; i++) {
            vt.out.print(" ");
        }

        return i;
    }

    private void drawLineInner(String line, int hOffset, int col) {
        int penX = col;
        char[] lineCh = line.toCharArray();

        for (int i = hOffset; penX < vt.screenSize.column && i < lineCh.length; i++) {
            char ch = lineCh[i];

            if (ch == '\t') {
                penX += drawTabStop(penX);
            }
            else {
                vt.out.print(ch);
                penX++;
            }
        }
    }

    private void redrawDocument() {
        vt.cursorHome(2, 1);
        int vOffset = model.getScrollY();
        int hOffset = model.getScrollX();

        for(int y = 0; y < (vt.screenSize.row - 1) && (y + vOffset) < model.getLineCount(); y++) {
            if (y != 0) vt.out.println();

            String line = model.getLine(y + vOffset);
            drawLineInner(line, hOffset, 0);

            vt.eraseEndOfLine();
        }
        vt.eraseDown();
        redrawGuides();
    }

    private void redrawLine(int l) {
        int vOffset = model.getScrollY();
        int hOffset = model.getScrollX();
        vt.cursorHome(l + 2, 1);
        drawLineInner(model.getLine(l + vOffset), hOffset, 0);
        vt.eraseEndOfLine();
        redrawGuidesLine(l + 2);
    }

    private void redrawLineFrom(int l, int c) {
        String line = model.getLine(l);
        int realC = 0;

        for (int i = 0; i < c; i++) {
            if (line.charAt(i) == '\t') {
                int tabSize = tabStop - (realC % tabStop);
                realC += tabSize;
            }
            else {
                realC += 1;
            }
        }

        drawLineInner(line, c, realC);
        vt.eraseEndOfLine();
        redrawGuidesLine(l + 2);
    }

    /**
     * Redraw the screen.
     */
    public void redraw() {
        vt.scrollScreen(2, vt.screenSize.row);
        redrawTitle();
//        redrawCoords(model.getScreenCursorX(), model.getScreenCursorY());
        redrawDocument();
        redrawGuides();
        int vOffset = model.getScrollY();
        vt.cursorHome((model.getCursorY() - vOffset) + 2, (model.getCursorX() - originX) + 1);
    }

    /**
     * Constructs a new view for the given model.
     * @param model The model document to draw.
     * @param state The state to allow for notification on when the file is changed.
     * @param vt The VT to render to.
     */
    public ADocumentView(ADocument model, AState state, VT vt) {
        originX = 0;
        originY = 0;
        this.model = model;
        this.vt = vt;
        this.model.addChangeListener(this);
        this.model.setCharWidth(1);
        this.model.setLineHeight(1);
        this.model.setLinesPerPage(vt.screenSize.row - 2);
        this.tabStop = model.getTabStop();

        state.addListener(this);
        docTitle = "[New File]";
        coordPos = vt.screenSize.column - 10;
    }

    @Override
    public void textInserted(int col, int line, char ch) {
        redrawLineFrom(line, col - 1);
    }

    @Override
    public void textRemoved(int col, int line) {
        int row = model.getScreenCursorY() - model.getScrollY();
        int column = model.getScreenCursorX() - model.getScrollX();
        vt.cursorHome(row + 2, column + 1);
        redrawLineFrom(line, col);
    }

    @Override
    public void lineInserted(int line) {
        // TODO: This can be done without redrawing the document, DO IT
        vt.eraseDown();
        redrawDocument();

        int vOffset = model.getScrollY();
        vt.cursorHome((line - vOffset) + 2, 0);
    }

    @Override
    public void lineRemoved(int line) {
        vt.eraseDown();
        redrawDocument();
    }

    @Override
    public void positionChanged(int col, int line) {
        int screenY = model.getScreenCursorY();
        int screenX = model.getScreenCursorX();

//        redrawCoords(screenX, screenY);
        int row = screenY - model.getScrollY();
        int column = screenX - model.getScrollX();
        vt.cursorHome(row + 2, column + 1);
    }

    @Override
    public void documentScrolled(int deltaX, int deltaY) {
        if (deltaY > 0) {
            vt.cursorHome(vt.screenSize.row, 1);
            vt.scrollDown();
            redrawLine(vt.screenSize.row - 2);
        }
        else if (deltaY < 0) {
            vt.cursorHome(2, 0);
            vt.scrollUp();
            redrawLine(0);
        }
    }

    @Override
    public void documentContentsReplaced() {

    }

    @Override
    public void tabStopChanged(int tabStop) {
        this.tabStop = tabStop;

        int row = model.getScreenCursorY() - model.getScrollY();
        int column = model.getScreenCursorX() - model.getScrollX();
        vt.cursorHome(row + 2, column + 1);
    }

    @Override
    public void newFile() {

    }

    @Override
    public void fileOpened(String filename) {
        docTitle = filename;
        redrawTitle();
    }

    @Override
    public void viewIsFlipped(int viewMode) {

    }

    @Override
    public void promptOpened(APrompt p) {

    }

    @Override
    public void promptClosed() {

    }
}
