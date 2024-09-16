package com.bravotic.ate.swing.themes;

import com.bravotic.ate.model.APrompt;
import com.bravotic.cam.swing.themes.CMenuPlan9Theme;

import javax.swing.*;
import java.awt.*;

/**
 * A theme modeled after the colors and design of Plan 9 by Bell Labs.
 */
public class Plan9Theme extends CMenuPlan9Theme implements ATETheme {
    @Override
    public Color getADocumentBackgroundColor() {
        return new Color(0xf8fce8);
    }

    @Override
    public void drawDocumentFrame(Graphics g, int width, int height) {

    }

    @Override
    public void drawPromptFrame(APrompt model, Graphics g, Rectangle d) {

    }

    @Override
    public void drawButton(Graphics g, String text, boolean selected, Rectangle d, FontMetrics fm) {

    }

    @Override
    public Icon[] getGlyphIcons() {
        return new Icon[0];
    }

    @Override
    public Color getAPromptBackgroundColor() {
        return null;
    }
}
