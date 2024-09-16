package com.bravotic.ate.swing.view;

import com.bravotic.ate.model.APrompt;
import com.bravotic.ate.model.APromptChangeListener;
import com.bravotic.ate.swing.controller.APromptController;
import com.bravotic.ate.swing.themes.APromptTheme;

import javax.swing.*;
import java.awt.*;

/**
 * Allows an APrompt to be drawn with Swing.
 */
public class APromptView extends JPanel implements APromptChangeListener {
    private APrompt model;
    private final FontMetrics fm;
    private final Font font;

    private Icon[] glyphIcons;

    private final int lineHeight;

    private final APromptController controller;

    private APromptTheme theme;

    /**
     * Constructs a new APromptView for the given model.
     * @param model The model for the view.
     */
    public APromptView(APrompt model) {
        this.model = model;
        controller = new APromptController(model);
        model.addChangeListener(this);

        this.font = new Font(Font.MONOSPACED, Font.PLAIN, 12);
        this.fm = getFontMetrics(font);
        lineHeight = fm.getMaxAscent() + fm.getMaxDescent();

        addKeyListener(controller);
    }

    /**
     * Construct a new view using the given theme to draw.
     * @param theme The theme to be used to draw.
     */
    public APromptView(APromptTheme theme) {
        this.model = null;
        this.theme = theme;
        controller = new APromptController();

        this.font = new Font(Font.MONOSPACED, Font.PLAIN, 12);
        this.fm = getFontMetrics(font);
        lineHeight = fm.getMaxAscent() + fm.getMaxDescent();

        setBackground(theme.getAPromptBackgroundColor());

        addKeyListener(controller);
        addMouseListener(controller);
        addMouseWheelListener(controller);

        glyphIcons = theme.getGlyphIcons();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        int y;
        int leftX;
        Dimension viewSize = getSize();
        int rectpadding = 5;
        int padding = 10;

        Graphics2D g2 = (Graphics2D)g;
        g2.setFont(font);

        g2.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

        // At the moment, our frame is 500 width by 150 height, so hardcode these values for now, this will change in
        // the future so don't get too attached.
        y = (viewSize.height / 2) - (150/2 + padding);
        leftX = (viewSize.width / 2) - (500/2 + padding);

        theme.drawPromptFrame(model, g, new Rectangle(leftX, y, 500 + (padding *2), 150 + (padding *2)));

        y += padding;

        // Draw our little glyph icon on the screen.
        int glyphGap = 10;
        Icon glyph = glyphIcons[model.getType()];
        int glyphWidth = glyph.getIconWidth() + glyphGap;

        glyph.paintIcon(this, g2, leftX + padding, y);

        int restWidth = 500 - glyphWidth;

        g2.drawString(model.getMessage(), leftX + glyphWidth + padding, y + lineHeight);

        y += (2*rectpadding) + lineHeight;

        // Draw our options at the bottom of the prompt.
        String[] options = model.getOptions();

        // We have a button prompt.
        if (options != null && options.length != 0) {
            // Each of our buttons is divided equally in the space, so each width is the total width / size
            int optionWidth = restWidth / options.length;

            // We start our buttons after the glyph, so our starting X is our leftX plus the glyph's width and the
            // frame's padding.
            int x = glyphWidth + padding + leftX;
            int selected = model.getSelected();

            // The bounds of our button. We draw buttons side by side (might change) so this is divided equally for
            // every button.
            Rectangle button = new Rectangle(x, y, optionWidth, lineHeight + (2 * rectpadding));

            for(int i = 0; i < options.length; i++) {
                // Draw the button from the theme
                theme.drawButton(g, options[i], i == selected, button, fm);

                // Move our bounds over by the width of the button.
                button.translate(optionWidth, 0);
            }
        }

        // We have a text prompt.
        else {
            g2.drawRect(leftX+ glyphWidth + padding, y, restWidth, lineHeight + 2*rectpadding);
            g2.drawString(model.getPromptText() + "|", leftX + rectpadding + glyphWidth + padding, y + rectpadding + lineHeight);
        }
    }

    /**
     * Change which model is being drawn.
     * @param model The updated model to be drawn.
     */
    public void setModel(APrompt model) {
        this.model = model;
        controller.setModel(model);
        model.addChangeListener(this);
        this.repaint();
    }

    @Override
    public void promptTextChanged(String text) {
        this.repaint();
    }

    @Override
    public void promptSelectionChanged(int selected) {
        this.repaint();
    }

    @Override
    public void promptSubmitted() {

    }
}
