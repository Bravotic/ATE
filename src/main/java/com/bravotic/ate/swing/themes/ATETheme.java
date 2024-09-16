package com.bravotic.ate.swing.themes;

import com.bravotic.cam.swing.themes.CMenuTheme;

import java.awt.*;

/**
 * Represents a theme for the complete ATE package.
 */
public interface ATETheme extends CMenuTheme, APromptTheme {
    /**
     * Get the background color of the ADocument.
     * @return The color of the background of the document view.
     */
    Color getADocumentBackgroundColor();

    /**
     * Draw the frame which the ADocument should draw.
     * @param g The graphics object to do the drawing.
     * @param width The width of the frame.
     * @param height The height of the frame.
     */
    void drawDocumentFrame(Graphics g, int width, int height);
}
