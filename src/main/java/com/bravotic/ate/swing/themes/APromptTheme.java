package com.bravotic.ate.swing.themes;

import com.bravotic.ate.model.APrompt;

import java.awt.*;
import javax.swing.*;

/**
 * Represents a theme for an APrompt.
 */
public interface APromptTheme {
    /**
     * Draws the frame around the prompt.
     * @param model The model of the prompt.
     * @param g The graphics object to do the drawing.
     * @param d The bounds of the frame.
     */
    void drawPromptFrame(APrompt model, Graphics g, Rectangle d);

    /**
     * Draws a button.
     * @param g The graphics object to do the drawing.
     * @param text The text of the button.
     * @param selected Whether the drawn button is selected.
     * @param d The bounds of the button.
     * @param fm The metrics of the font being used.
     */
    void drawButton(Graphics g, String text, boolean selected, Rectangle d, FontMetrics fm);

    /**
     * Gets an array of the glyph icons being used.
     * @return The glyph icons being used.
     */
    Icon[] getGlyphIcons();

    /**
     * Get the background color of the prompt.
     * @return The background color of the prompt.
     */
    Color getAPromptBackgroundColor();
}
