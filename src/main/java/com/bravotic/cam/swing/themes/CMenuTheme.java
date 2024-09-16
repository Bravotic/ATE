package com.bravotic.cam.swing.themes;

import com.bravotic.cam.model.CMenu;
import com.bravotic.cam.swing.view.CMenuView;

import java.awt.*;

/**
 * Represents a theme for a CMenuView.
 */
public interface CMenuTheme {
    CMenuView createNewView(CMenu menu);
    Color getMenuContainerBackgroundColor();

}
