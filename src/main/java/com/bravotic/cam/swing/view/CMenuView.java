package com.bravotic.cam.swing.view;

import java.awt.*;
import javax.swing.*;

import com.bravotic.cam.model.CMenu;
import com.bravotic.cam.model.CMenuChangeListener;
import com.bravotic.cam.model.CMenuItem;

// TODO: This should NOT be implemented like this. This is a failed attempt at trying to make the theme use decorator
// pattern... Needs to be brought in line with how the other widgets do themes.
public abstract class CMenuView extends JPanel implements CMenuChangeListener {
    protected final CMenu menu;

    protected Dimension itemSize;

    protected int padding = 10;

    protected int margin = 10;

    protected Font font;

    protected Insets frameInsets;

    private String searchTerm;

    protected int frameHeight;
    protected int frameWidth;

    protected abstract void drawSearchMenuItem(Graphics2D g2, int x, int y, String label);

    protected abstract void drawMenuItem(Graphics2D g2, int x, int y, CMenuItem item);

    protected abstract void drawSelectedMenuItem(Graphics2D g2, int x, int y, CMenuItem item);

    protected abstract void drawMenuFrame(Graphics2D g2, int x, int y, int w, int h);

    protected void updateFrameSize() {
        frameHeight = (menu.maxSize() + 1) * ((2 * padding) + itemSize.height);
        frameWidth = (2*padding) + itemSize.width;
        this.setMaximumSize(new Dimension((2*margin) + (frameInsets.left + frameInsets.right) + frameWidth,
                (2*margin) + (frameInsets.top + frameInsets.bottom) + frameHeight));

    }

    public CMenuView(CMenu menu) {
        this.menu = menu;
        this.setFocusable(false);
        this.setDoubleBuffered(true);
        itemSize = new Dimension();
        searchTerm = menu.getSearchTerm();
        menu.addCMenuChangeListener(this);

        setOpaque(false);
        setBackground(new Color(0, 0, 0, 0));
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // TODO: It may be a good idea to prerender the menu options and save them to a BufferedImage until they need
        // to be redrawn, like if they are selected.

        Graphics2D g2 = (Graphics2D)g;

        g2.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

        // Start our x and y taking account of our margins
        int x = margin + frameInsets.left;
        int y = margin + frameInsets.top;

        // Establish our effective item width. Literally just the item size plus the padding that goes around it.
        int effectiveItemWidth = (2*padding) + itemSize.width;
        int effectiveItemHeight = (2*padding) + itemSize.height;

        // Calculate our frame height and width. This is the total drawn area by JUST the items. The frame will likely
        // be larger than this if frameInsets were given.
        frameHeight = (menu.size() + 1) * effectiveItemHeight;
        frameWidth = effectiveItemWidth;

        // Update our page size
        // TODO: this should only happen when we change our size
        this.menu.setPageSize(this.getHeight() / effectiveItemHeight);

        // Get which item is currently selected
        int selected = menu.getSelected();

        // See if that selected item is off the screen
        int selectedStartingY = margin + (selected * effectiveItemHeight);

        // If it is off the screen, fix our Y such that it is the last thing visible. In practice this scrolls the
        // screen as the user types.
        if (selectedStartingY > this.getHeight() - (2 * effectiveItemHeight)) {
            int fixedStartingY = this.getHeight() - (2 * effectiveItemHeight);
            y -= (selectedStartingY - fixedStartingY);
        }

        g2.setFont(font);

        // The menu frame is drawn first
        drawMenuFrame(g2, x, y, frameWidth, frameHeight);

        // If there is text in the search bar, display it plus a carat. If not, display the text "Type to search"
        if (searchTerm.isEmpty()) {
            drawSearchMenuItem(g2, x, y, "Type to search");
        }
        else {
            drawSearchMenuItem(g2, x, y, searchTerm + "|");
        }

        y += effectiveItemHeight;

        // Loop over drawing all of our menu items.
        // FIXME: This can likely be stopped early to only draw what is on screen.
        for (int i = 0; i < menu.size(); i++) {
            CMenuItem item = menu.get(i);

            if (i == selected) {
                drawSelectedMenuItem(g2, x, y, item);
            }
            else {
                drawMenuItem(g2, x, y, item);
            }

            y += effectiveItemHeight;
        }
    }

    @Override
    public void removeNotify() {
        Graphics g = getGraphics();
        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());
        super.removeNotify();
    }

    @Override
    public void CMenuItemAdded(CMenuItem c) {
        updateFrameSize();
    }

    @Override
    public void SelectionChanged(int selected) {
        this.repaint();
    }

    @Override
    public void CMenuItemSelected(CMenuItem c, boolean keepMenuOpen) {
    }

    @Override
    public void CMenuSearchTermChanged(String term) {
        searchTerm = term;
        this.repaint();
    }

    @Override
    public boolean CMenuGoBack() {
        return false;
    }

}
