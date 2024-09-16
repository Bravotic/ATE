package com.bravotic.cam.swing.themes;

import com.bravotic.cam.model.CMenu;
import com.bravotic.cam.model.CMenuItem;
import com.bravotic.cam.swing.view.CMenuView;

import javax.swing.*;
import java.awt.*;

public class CMenuNativeTheme implements CMenuTheme {
    private class NativeMenuView extends CMenuView {

        JPopupMenu delegate;

        JMenuItem standardItem;
        JMenu subdirItem;

        JTextField search;

        private final FontMetrics fm;

        protected void drawSearchMenuItem(Graphics2D g2, int x, int y, String label) {
            g2.setClip(null);

            Insets s = search.getBorder().getBorderInsets(search);
            Graphics b = g2.create();
            b.translate(x + s.left, y + s.top);

            search.setMaximumSize(new Dimension((itemSize.width + (2 * padding)) - (s.left + s.right), (itemSize.height + (padding * 2)) - (s.top + s.bottom)));
            search.setBounds(0,0, (itemSize.width + (2 * padding)) - (s.left + s.right), (itemSize.height + (padding * 2)) - (s.top + s.bottom));

            search.setText(label);
            search.paint(b);
            b.dispose();
        }

        protected void drawMenuItem(Graphics2D g2, int x, int y, CMenuItem item) {
            g2.setClip(null);

            AbstractButton model;
            if (item.hasSubmenu) {
                model = subdirItem;
            }
            else {
                model = standardItem;
            }

            Insets s = model.getBorder().getBorderInsets(model);

            Graphics b = g2.create();
            b.translate(x + s.left, y + s.top);

            model.getModel().setArmed(false);
            model.getModel().setRollover(false);
            model.getModel().setSelected(false);
            model.getModel().setPressed(false);
            model.setText(item.label);
            model.setMaximumSize(new Dimension((itemSize.width + (2 * padding)) - (s.left + s.right), (itemSize.height + (padding * 2)) - (s.top + s.bottom)));
            model.setBounds(0,0, (itemSize.width + (2 * padding)) - (s.left + s.right), (itemSize.height + (padding * 2)) - (s.top + s.bottom));
            model.paint(b);

            b.dispose();

        }

        protected void drawSelectedMenuItem(Graphics2D g2, int x, int y, CMenuItem item) {
            g2.setClip(null);

            AbstractButton model;
            if (item.hasSubmenu) {
                model = subdirItem;
            }
            else {
                model = standardItem;
            }

            Insets s = model.getBorder().getBorderInsets(model);

            Graphics b = g2.create();
            b.translate(x + s.left, y + s.top);

            model.getModel().setArmed(true);
            model.getModel().setRollover(true);
            model.getModel().setSelected(true);
            model.getModel().setPressed(true);
            model.setText(item.label);
            //model.setSize(new Dimension((width + (2 * padding)) - (s.left + s.right), (height + (padding * 2)) - (s.top - s.bottom)));
            model.setMaximumSize(new Dimension((itemSize.width + (2 * padding)) - (s.left + s.right), (itemSize.height + (padding * 2)) - (s.top + s.bottom)));
            model.setBounds(0,0, (itemSize.width + (2 * padding)) - (s.left + s.right), (itemSize.height + (padding * 2)) - (s.top + s.bottom));

            model.paint(b);

            b.dispose();
        }

        protected void drawMenuFrame(Graphics2D g2, int x, int y, int w, int h) {
            Graphics b = g2.create();

            b.translate(x - frameInsets.left, y - frameInsets.top);
            delegate.setPopupSize(w, h);
            delegate.setBounds(0, 0,
                    w + frameInsets.left + frameInsets.right, h + frameInsets.top + frameInsets.bottom);

            delegate.paint(b);
            b.dispose();
        }

        public NativeMenuView(CMenu menu) {
            super(menu);

            delegate = new JPopupMenu();
            standardItem = new JMenuItem();
            subdirItem = new JMenu();
            search = new JTextField(80);
            frameInsets = delegate.getInsets();

            fm = getFontMetrics(standardItem.getFont());

            itemSize.height = fm.getHeight();
            itemSize.width = 200;

            for (int i = 1; i < menu.size(); i++) {
                int width = fm.stringWidth(menu.get(i).label);

                if(width > itemSize.width) {
                    itemSize.width = width;
                }
            }
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

    protected JPanel reference;

    private void LAFSelector() {
        // Temporary code to allow for switching LAF for testing
        UIManager.LookAndFeelInfo[] looks = UIManager.getInstalledLookAndFeels();
        String[] looksAsString = new String[looks.length];

        int i = 0;
        for (UIManager.LookAndFeelInfo look : looks) {
            looksAsString[i++] = look.getClassName();
        }

        String laf = (String)JOptionPane.showInputDialog(
                null,
                "Select a look and feel",
                "Dialog",
                JOptionPane.PLAIN_MESSAGE,
                null,
                looksAsString,
                "");

        try {
            UIManager.setLookAndFeel(laf);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                 UnsupportedLookAndFeelException e) {
            throw new RuntimeException(e);
        }
    }

    private final Color menuContainerBackground;

    public CMenuNativeTheme() {
        LAFSelector();
        menuContainerBackground = new JPanel().getBackground();
    }

    @Override
    public CMenuView createNewView(CMenu menu) {
        return new NativeMenuView(menu);
    }

    @Override
    public Color getMenuContainerBackgroundColor() {
        return menuContainerBackground;
    }
}
