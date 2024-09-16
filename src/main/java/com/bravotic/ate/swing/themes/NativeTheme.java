package com.bravotic.ate.swing.themes;

import com.bravotic.ate.model.APrompt;
import com.bravotic.cam.swing.themes.CMenuNativeTheme;

import javax.swing.*;
import java.awt.*;

/**
 * A theme which uses Swing to draw native widgets.
 */
public class NativeTheme extends CMenuNativeTheme implements ATETheme {
    private final Color documentBackgroundColor;
    private final JTextArea reference;
    private final JInternalFrame promptReference;

    private final JButton buttonReference;

    private final Icon[] glyphIcons;

    /**
     * Construct a new native theme.
     */
    public NativeTheme() {

        glyphIcons = new Icon[4];

        glyphIcons[APrompt.INFO] = UIManager.getIcon("OptionPane.informationIcon");
        glyphIcons[APrompt.QUESTION] = UIManager.getIcon("OptionPane.questionIcon");
        glyphIcons[APrompt.WARNING] = UIManager.getIcon("OptionPane.warningIcon");
        glyphIcons[APrompt.ERROR] = UIManager.getIcon("OptionPane.errorIcon");
        reference = new JTextArea();
        promptReference = new JInternalFrame("Placeholder Title Text", false, false);
        promptReference.setBackground(reference.getBackground());
        documentBackgroundColor = reference.getBackground();
        buttonReference = new JButton();
    }

    @Override
    public Color getADocumentBackgroundColor() {
        return documentBackgroundColor;
    }

    @Override
    public void drawDocumentFrame(Graphics g, int width, int height) {
        reference.setSize(width, height);
        reference.paint(g);
    }

    @Override
    public void drawPromptFrame(APrompt model, Graphics g, Rectangle r) {
        Insets pi = promptReference.getInsets();
        Graphics offset = g.create(r.x - pi.left,
                r.y - pi.top,
                r.width + (pi.left + pi.right),
                r.height + (pi.top + pi.bottom));
        promptReference.setSize(r.width + (pi.left + pi.right),
                r.height + (pi.top + pi.bottom));

        switch (model.getType()) {
            case APrompt.INFO:
                promptReference.setTitle("Information");
                break;
            case APrompt.QUESTION:
                promptReference.setTitle("Question");
                break;
            case APrompt.WARNING:
                promptReference.setTitle("Warning");
                break;
            case APrompt.ERROR:
                promptReference.setTitle("Error");
                break;
        }

        promptReference.paint(offset);
    }

    @Override
    public void drawButton(Graphics g, String text, boolean selected, Rectangle d, FontMetrics fm) {
        Graphics offset = g.create(d.x, d.y, d.width, d.height);
        buttonReference.setSize(d.width, d.height);
        buttonReference.setText(text);

        buttonReference.getModel().setArmed(selected);
        buttonReference.getModel().setRollover(selected);
        buttonReference.getModel().setSelected(selected);
        buttonReference.getModel().setPressed(selected);

        buttonReference.paint(offset);
    }

    @Override
    public Icon[] getGlyphIcons() {


        return glyphIcons;
    }

    @Override
    public Color getAPromptBackgroundColor() {
        return new JFrame().getBackground();
    }
}
