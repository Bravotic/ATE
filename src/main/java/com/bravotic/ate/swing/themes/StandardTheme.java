package com.bravotic.ate.swing.themes;

import com.bravotic.ate.model.APrompt;
import com.bravotic.cam.swing.themes.CMenuStandardTheme;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * A monochrome standard theme.
 */
public class StandardTheme extends CMenuStandardTheme implements ATETheme {

    private Icon renderGlyph(char glyphChar) {
        BufferedImage glyph;

        // TODO: Is this canvas needed? We are using it just to get font metrics
        Canvas c = new Canvas();
        Font glyphFont = new Font(Font.SERIF, Font.BOLD, 48);
        FontMetrics m = c.getFontMetrics(glyphFont);

        int padding = 10;
        int margin = 0;
        int boxSize = Math.max(m.getAscent() + m.getDescent(), m.charWidth(glyphChar)) + (padding * 2);

        Polygon glyphPolygon = new Polygon();

        glyphPolygon.addPoint(margin + boxSize / 2, margin);
        glyphPolygon.addPoint(margin + boxSize, margin + boxSize / 2);
        glyphPolygon.addPoint(margin + boxSize/2, margin + boxSize);
        glyphPolygon.addPoint(margin,margin + boxSize/2);

        glyph = new BufferedImage(boxSize, boxSize, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = (Graphics2D)glyph.getGraphics();
        g2.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

        g2.setFont(glyphFont);
        g2.setColor(Color.BLACK);
        g2.fillPolygon(glyphPolygon);
        g2.setColor(Color.white);
        g2.drawString(Character.toString(glyphChar), margin + (boxSize/2) - (m.charWidth(glyphChar)/2), margin + m.getAscent() + padding);
        g2.dispose();

        return new ImageIcon(glyph);
    }

    @Override
    public Color getADocumentBackgroundColor() {
        return new Color(0xFFFFFF);
    }

    @Override
    public void drawDocumentFrame(Graphics g, int width, int height) {

    }


    @Override
    public void drawPromptFrame(APrompt model, Graphics g, Rectangle d) {
        Graphics2D g2 = (Graphics2D)g;
        g2.setColor(Color.BLACK);
        g2.drawRect(d.x, d.y, d.width, d.height);
    }

    @Override
    public void drawButton(Graphics g, String text, boolean selected, Rectangle d, FontMetrics fm) {
        Graphics2D g2 = (Graphics2D)g;
        if (selected) {
            g2.setColor(Color.BLACK);
            g2.drawRect(d.x, d.y, d.width, d.height);
            g2.fillRect(d.x, d.y, d.width, d.height);
            g2.setColor(Color.white);
        }
        else {
            g2.setColor(Color.BLACK);
            g2.drawRect(d.x, d.y, d.width, d.height);
        }
        g2.drawString(text, d.x + 5, d.y + fm.getAscent() + 5);
    }

    @Override
    public Icon[] getGlyphIcons() {
        Icon[] glyphIcons = new Icon[4];

        glyphIcons[APrompt.INFO] = renderGlyph('i');
        glyphIcons[APrompt.QUESTION] = renderGlyph('?');
        glyphIcons[APrompt.WARNING] = renderGlyph('!');
        glyphIcons[APrompt.ERROR] = renderGlyph('X');

        return glyphIcons;
    }

    @Override
    public Color getAPromptBackgroundColor() {
        return new Color(0xFFFFFF);
    }
}
