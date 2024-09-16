package com.bravotic.cam.swing.view;

import com.bravotic.cam.model.*;
import com.bravotic.cam.swing.controller.CMenuController;
import com.bravotic.cam.swing.themes.CMenuStandardTheme;
import com.bravotic.cam.swing.themes.CMenuTheme;

import javax.swing.*;
import java.awt.*;
import java.util.Stack;


public class CMenuContainerView extends JPanel implements CMenuContainerListener {
    Stack<CMenuView> views;

    CMenuController controller;

    private final CMenuTheme theme;

    private final CMenuContainer model;

    protected void layoutMenus() {
        int x = 0;
        int y = 0;

        int totalWidth = 0;
        for (CMenuView v : views) {
            Dimension vDim = v.getMaximumSize();
            v.setSize(vDim.width, Math.min(vDim.height, this.getHeight()));

            totalWidth += v.getWidth();
        }

        if (totalWidth > this.getSize().width) {
            x = this.getSize().width;
            for (int i = views.size() - 1; i >= 0; i--) {
                CMenuView v = views.get(i);
                x -= v.getWidth();
                v.setLocation(x, y);
            }
        }
        else {
            for (CMenuView v : views) {
                v.setLocation(x, y);
                x += v.getWidth();
            }
        }


    }

    public CMenuContainerView(CMenuContainer model) {
        this(model, new CMenuStandardTheme());
    }

    public CMenuContainerView(CMenuContainer model, CMenuTheme theme) {
        this.setLayout(null);
        this.theme = theme;
        this.model = model;

        model.addListener(this);

        views = new Stack<>();

        CMenu root = model.getTopMenu();

        CMenuView view = theme.createNewView(root);
        this.controller = new CMenuController(root);

        views.push(view);
        this.add(view);

        this.setBackground(theme.getMenuContainerBackgroundColor());

        this.layoutMenus();
        this.repaint();

        this.setFocusable(true);
        this.setFocusTraversalKeysEnabled(false);

        this.addMouseWheelListener(controller);
        this.addMouseListener(controller);
        this.addKeyListener(controller);
    }

    @Override
    public void setBounds(int x, int y, int w, int h) {
        super.setBounds(x, y, w, h);
        layoutMenus();
    }

    @Override
    public void submenuOpened(String label, CMenu menu) {
        CMenuView view = theme.createNewView(menu);
        views.push(view);
        this.add(view);

        this.layoutMenus();
        this.repaint();

        controller.setCurrentMenu(menu);
    }

    @Override
    public void submenuClosed(CMenu menu) {
        this.remove(this.views.pop());

        layoutMenus();
        this.repaint();

        this.controller.setCurrentMenu(model.getTopMenu());
    }

    @Override
    public void menuWouldLikeToClose() {

    }
}
