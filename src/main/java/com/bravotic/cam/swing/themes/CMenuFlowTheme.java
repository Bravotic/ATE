package com.bravotic.cam.swing.themes;

import com.bravotic.cam.model.CMenu;
import com.bravotic.cam.model.CMenuItem;
import com.bravotic.cam.swing.view.CMenuView;

import java.awt.*;

public class CMenuFlowTheme implements CMenuTheme {
    private class CMenuViewFlowTheme extends CMenuView {

        private final int subdirMarkerWidth;
        private final FontMetrics fm;

        protected void drawSearchMenuItem(Graphics2D g2, int x, int y, String label) {
            g2.setClip(x, y, itemSize.width + (2*padding), itemSize.height + (2*padding));
            g2.setColor(Color.BLACK);
            g2.drawString(label, x + padding, y + itemSize.height + padding);
        }

        protected void drawMenuItem(Graphics2D g2, int x, int y, CMenuItem item) {
            g2.setClip(x, y, itemSize.width + (2*padding), itemSize.height + (2*padding));

            g2.setColor(Color.BLACK);
            g2.drawLine(x, y, x + itemSize.width + (2*padding), y);

            if (item.hasSubmenu) {
                g2.drawString("»", x + padding, y + itemSize.height + padding);
            }
            g2.drawString(item.label, x + padding + subdirMarkerWidth, y + itemSize.height + padding);


        }

        protected void drawSelectedMenuItem(Graphics2D g2, int x, int y, CMenuItem item) {
            g2.setClip(x, y, itemSize.width + (2*padding), itemSize.height + (2*padding));

            g2.setColor(Color.DARK_GRAY);
            g2.fillRect(x, y, itemSize.width + (2 * padding), itemSize.height + (padding * 2));
            g2.setColor(Color.WHITE);

            if (item.hasSubmenu) {
                g2.drawString("»", x + padding, y + itemSize.height + padding);
            }
            g2.drawString(item.label, x + padding + subdirMarkerWidth, y + itemSize.height + padding);
        }

        protected void drawMenuFrame(Graphics2D g2, int x, int y, int w, int h) {
            g2.setColor(Color.LIGHT_GRAY);

            g2.fillRect(x + 3, y + 3, w, h);

            g2.setColor(Color.WHITE);
            g2.fillRect(x, y, w, h);

            g2.setColor(Color.GRAY);
            g2.drawRect(x, y, w, h);



        }

        protected Dimension getItemSize(CMenuItem item) {
            return new Dimension(fm.stringWidth(item.label) + subdirMarkerWidth, fm.getHeight());
        }

        public CMenuViewFlowTheme(CMenu menu) {
            super(menu);

            font = new Font(Font.SANS_SERIF, Font.PLAIN, 12);
            fm = this.getFontMetrics(font);

            subdirMarkerWidth = fm.stringWidth("» ");

            // Find our largest width, and set our width to that.
            itemSize.width = 200;
            itemSize.height = fm.getHeight();

            for (int i = 0; i < menu.size(); i++) {
                int width = fm.stringWidth(menu.get(i).label);

                if(width > itemSize.width) {
                    itemSize.width = width;
                }
            }

            frameInsets = new Insets(0, 0, 2, 2);

            updateFrameSize();
        }

        @Override
        public void CMenuItemAdded(CMenuItem c) {
            int thisWidth = fm.stringWidth(c.label);

            if (thisWidth > itemSize.width) {
                itemSize.width = thisWidth;
            }

            updateFrameSize();
        }
    }

    @Override
    public CMenuView createNewView(CMenu menu) {
        return new CMenuViewFlowTheme(menu);
    }

    @Override
    public Color getMenuContainerBackgroundColor() {
        return new Color(0xF5F5F5);
    }
}
