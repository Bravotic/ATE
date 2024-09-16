package com.bravotic.ate.swing;

import com.bravotic.ate.model.AState;
import com.bravotic.ate.swing.themes.ATETheme;
import com.bravotic.ate.swing.themes.FlowTheme;
import com.bravotic.ate.swing.view.AStateView;

import javax.swing.*;
import java.awt.*;

public class ATextEditorSwayFix {
    public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        ATETheme theme = new FlowTheme();
        AState state = new AState();
        AStateView window = new AStateView(state, theme);

        window.setPreferredSize(new Dimension(640, 480));

        // For Sway, we need to set the window to be undecorated and maximized for both width and height, or else it
        // will only render in a small 640x480 box. I'm not sure why this happens, but it seems to be sway specific.
        window.setUndecorated(true);
        window.pack();

        window.setLocationRelativeTo(null);

        window.setVisible(true);
        window.setExtendedState(JFrame.MAXIMIZED_BOTH);

    }
}
