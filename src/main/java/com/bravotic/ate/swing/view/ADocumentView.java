package com.bravotic.ate.swing.view;

import com.bravotic.ate.model.ADocument;
import com.bravotic.ate.model.ADocumentChangeListener;
import com.bravotic.ate.swing.controller.ADocumentController;
import com.bravotic.ate.swing.themes.ATETheme;
import com.bravotic.ate.swing.themes.StandardTheme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;
import java.util.ArrayDeque;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

/**
 * Represents a Swing based view for an ADocument.
 */
public class ADocumentView extends JPanel implements ADocumentChangeListener, MouseWheelListener, ComponentListener {
    private final ADocument model;
    private final Font font;
    private final int lineHeight;
    private final int lineMargin;
    private final int cursorHeight;
    private final int charWidth;
    private int tabWidthSize;
    private final Insets margin;

    // We specify refresh rate for now as 240 since it seems to work best in most if not all cases. We can use java to
    // get this information, but it has not proven to work well.
    private final int refreshRate = 240;

    /**
     * Represents the point on the screen to draw the cursor.
     * NOTE: Cursor point now translates AS the page scrolls. This makes everything simpler than what it used to be.
     * Check the documentScrolled notification for the change in its position.
     */
    private final Point cursorPoint;

    private final ArrayDeque<Rectangle> redrawRequests;

    /**
     * The back buffer used to render our text.
     */
    private Image textBuffer;

    /**
     * The scale of the buffer.
     */
    private double bufferScale;

    private void redrawTextRegion(Graphics2D g, Rectangle bounds) {

        // Clear the text region we want to redraw
        g.clearRect(bounds.x, bounds.y, bounds.width, bounds.height);

        // Calculate our starting index, aka the line which is the first line visible in the viewport.
        int si = (int)Math.round((double)((model.getScrollY() - margin.top) + bounds.y) / lineHeight);
        for (int a = 0; si + a < model.getLineCount() && a * lineHeight < bounds.height; a++) {

            // Get our current line
            char[] line = model.getLine(si + a).toCharArray();
            int penX = 0;
            int penY = (((si + a) * lineHeight) + margin.top + cursorHeight) - model.getScrollY();

            for (int lineIndex = 0; lineIndex < line.length; lineIndex++) {
                if (line[lineIndex] == '\t') {
                    int tabSize = tabWidthSize - (penX % tabWidthSize);
                    penX += tabSize;
                }
                // If we are in a region before the clip bound, just increment the penX and don't bother running
                // drawing code.
                else if (penX + charWidth < bounds.x) {
                    penX += charWidth;
                }
                else if (penX > bounds.x + bounds.width) {
                    break;
                }
                else {
                    g.drawString(String.valueOf(line[lineIndex]), margin.left + penX, penY);
                    penX += charWidth;
                }
            }
        }
    }

    private void recreateTextBuffer(Component c) {
        int width = c.getWidth();
        int height = c.getHeight();

        // Get our scale for the graphics configuration. This is the scale that our text needs to be scaled by.
        double scale = c.getGraphicsConfiguration().getDefaultTransform().getScaleX();
        bufferScale = scale;

        int bufferWidth = (int)Math.round((width - (margin.left + margin.right)) * scale);
        int bufferHeight = (int)Math.round((height - (margin.left + margin.right)) * scale);

        // Create a new buffered image for our new window size.
        Image newTextBuffer = this.createImage(bufferWidth, bufferHeight);

        // Draw the contents of the old buffer to the new buffer
        if (textBuffer != null) {
            Graphics2D g2 = (Graphics2D) newTextBuffer.getGraphics();
            g2.drawImage(textBuffer, 0, 0, null);
            g2.dispose();
        }

        // Replace the old buffer with the new buffer
        textBuffer = newTextBuffer;

        redrawRequests.add(new Rectangle(0, 0, bufferWidth, bufferHeight));
    }

    /**
     * Constructs a new view for the given model.
     * @param model The model to be shown.
     */
    public ADocumentView(ADocument model) {
        this(model, new StandardTheme());
    }

    /**
     * Constructs a new view with a theme for the given model.
     * @param model The model to be shown.
     * @param theme The theme to be used to draw the model.
     */
    public ADocumentView(ADocument model, ATETheme theme) {
        this.model = model;

        this.font = new Font(Font.MONOSPACED, Font.PLAIN, 12);
        FontMetrics fm = getFontMetrics(font);

        lineMargin = 4;
        lineHeight = fm.getMaxAscent() + fm.getMaxDescent() + lineMargin;
        cursorHeight = fm.getMaxAscent() + fm.getMaxDescent();

        charWidth = fm.stringWidth("X");

        model.setLineHeight(lineHeight);
        model.setCharWidth(charWidth);

        this.addComponentListener(this);

        tabWidthSize = model.getTabStop() * charWidth;

        this.setBackground(theme.getADocumentBackgroundColor());

        cursorPoint = new Point(0, 0);

        ADocumentController controller = new ADocumentController(model);
        addKeyListener(controller);
        addMouseListener(controller);
        addMouseWheelListener(this);

        model.addChangeListener(this);

        redrawRequests = new ArrayDeque<>();

        setFocusable(true);
        //setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        margin = getInsets();

        int bufferHeight = Math.max(1, getHeight() - (margin.top + margin.bottom));
        int bufferWidth = Math.max(1, getWidth() - (margin.left + margin.right));
        textBuffer = new BufferedImage(bufferWidth, bufferHeight, BufferedImage.TYPE_INT_ARGB);
        bufferScale = 1;

        // This is a thread to periodically redraw text updates to our back buffer for text, then paintComponent draws
        // that to the actual widget. We do this because Java won't give us vsync control, and redrawing for every
        // character is not a good idea. Maybe at some point we transition this to a Swing Timer that we just keep
        // resetting?
        Thread redrawThread = new Thread(() -> {
            try {
                while (true) {
                    if (!redrawRequests.isEmpty()) {
                        Graphics2D g2 = (Graphics2D) textBuffer.getGraphics();
                        if (g2 != null) {
                            g2.setColor(Color.BLACK);
                            g2.scale(bufferScale, bufferScale);
                            g2.setFont(font);

                            g2.setRenderingHint(
                                    RenderingHints.KEY_TEXT_ANTIALIASING,
                                    RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

                            g2.setBackground(theme.getADocumentBackgroundColor());

                            Rectangle totalBounds = null;

                            synchronized (redrawRequests) {
                                for (Rectangle bounds : redrawRequests) {
                                    // If the bound we are wanting to draw is already in our total bounds, it means we
                                    // already drew it, so we can safely skip it.
                                    if (totalBounds != null && totalBounds.contains(bounds)) {
                                        continue;
                                    }

                                    g2.setClip(bounds);
                                    redrawTextRegion(g2, bounds);

                                    if (totalBounds != null)
                                        totalBounds = totalBounds.union(bounds);
                                    else
                                        totalBounds = bounds;
                                }

                                redrawRequests.clear();
                            }
                            g2.dispose();
                            repaint(totalBounds);
                        }
                    }

                    Thread.sleep(1000 / refreshRate);
                }
            } catch (InterruptedException ignored) {

            }
        });

        redrawThread.start();
    }

    @Override
    public void setBounds(int x, int y, int w, int h) {
        super.setBounds(x, y, w, h);

        int realW = getWidth();
        int realH = getHeight();



        // We subtract 1 to make sure we aren't treating a partial line as a whole line
        int maxLines = (realH / lineHeight) - 1;
        model.setLinesPerPage(maxLines);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        g2.setFont(font);
        g2.setColor(Color.BLACK);

        Rectangle clipBound = g2.getClipBounds();

        // Clear our clip bounds since I don't think all platforms were doing that before.
        g2.clearRect(clipBound.x, clipBound.y, clipBound.width, clipBound.height);

        g2.scale(1/bufferScale, 1/bufferScale);
        g2.drawImage(textBuffer, margin.left, margin.top, null);
        g2.scale(bufferScale, bufferScale);

        for (int guide : model.getGuides()) {
            Rectangle guideRect = new Rectangle((guide) * charWidth, 0, 1, getHeight());
            if (clipBound.intersects(guideRect)) {
                g2.drawLine(guideRect.x, guideRect.y, guideRect.x, guideRect.height);
            }
        }

        if (clipBound.contains(cursorPoint)) {
            g2.drawRect(cursorPoint.x + margin.left,
                    cursorPoint.y + margin.top,
                    1,
                    cursorHeight);
        }

        //g2.setColor(Color.red);
        //g2.drawRect(clipBound.x, clipBound.y, clipBound.width, clipBound.height);
    }

    @Override
    public void textInserted(int col, int line, char ch) {
        //repaintContentImmediately(cursorPoint.x, ((line * lineHeight) + lineMargin) - model.getScrollY(), getWidth(), cursorHeight);
        redrawRequests.addLast(new Rectangle(cursorPoint.x, ((line * lineHeight) + lineMargin) - model.getScrollY(), getWidth(), lineHeight));
    }

    @Override
    public void textRemoved(int col, int line) {
        //repaintContent(cursorPoint.x, ((line * lineHeight) + lineMargin) - model.getScrollY(), getWidth(), cursorHeight);
        redrawRequests.addLast(new Rectangle(cursorPoint.x - charWidth, ((line * lineHeight) + lineMargin) - model.getScrollY(), getWidth(), lineHeight));
    }

    @Override
    public void lineInserted(int line) {
        synchronized (redrawRequests) {
            redrawRequests.clear();
            redrawRequests.addLast(this.getVisibleRect());
        }
    }

    @Override
    public void lineRemoved(int line) {
        synchronized (redrawRequests) {
            redrawRequests.clear();
            redrawRequests.addLast(this.getVisibleRect());
        }
    }

    @Override
    public void positionChanged(int col, int line) {
        Point oldCursorPoint = new Point(cursorPoint.x, cursorPoint.y);


        cursorPoint.y = model.getScreenCursorY() - model.getScrollY();
        cursorPoint.x = model.getScreenCursorX() - model.getScrollX();

        redrawRequests.addLast(new Rectangle( cursorPoint.x - 5, cursorPoint.y - 1, 10, cursorHeight + 2));
        redrawRequests.addLast(new Rectangle( oldCursorPoint.x - 5, oldCursorPoint.y - 1, 10, cursorHeight + 2));
    }

    @Override
    public void documentScrolled(int deltaX, int deltaY) {
        cursorPoint.y = model.getScreenCursorY() - model.getScrollY();
        cursorPoint.x = model.getScreenCursorX() - model.getScrollX();

        synchronized (redrawRequests) {
            redrawRequests.clear();
            redrawRequests.addLast(this.getVisibleRect());
        }
    }

    @Override
    public void documentContentsReplaced() {
        synchronized (redrawRequests) {
            redrawRequests.clear();
            redrawRequests.addLast(this.getVisibleRect());
        }
    }

    @Override
    public void tabStopChanged(int tabStop) {
        tabWidthSize = tabStop * charWidth;

        cursorPoint.y = model.getScreenCursorY() - model.getScrollY();
        cursorPoint.x = model.getScreenCursorX() - model.getScrollX();
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        int multiplier = (int)Math.ceil(((double)e.getPoint().y / getHeight()) * 10);
        model.scrollY((int)Math.round(e.getPreciseWheelRotation() * multiplier));
    }

    @Override
    public void componentResized(ComponentEvent e) {
        recreateTextBuffer(e.getComponent());
    }

    @Override
    public void componentMoved(ComponentEvent e) {
        recreateTextBuffer(e.getComponent());
    }

    @Override
    public void componentShown(ComponentEvent e) {
        redrawRequests.addLast(new Rectangle(0, 0, textBuffer.getWidth(null), textBuffer.getHeight(null)));
    }

    @Override
    public void componentHidden(ComponentEvent e) {

    }
}
