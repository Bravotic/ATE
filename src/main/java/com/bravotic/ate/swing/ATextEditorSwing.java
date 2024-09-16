package com.bravotic.ate.swing;

import com.bravotic.ate.model.AState;
import com.bravotic.ate.swing.themes.*;
import com.bravotic.ate.swing.view.AStateView;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class ATextEditorSwing {
    public static void main(String[] args) {
        ATETheme theme = new FlowTheme();
        AState state = new AState();

        AStateView window = new AStateView(state, theme);

        window.setPreferredSize(new Dimension(640, 480));
        window.pack();

        window.setLocationRelativeTo(null);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setVisible(true);

        if (args.length != 0) {
            try {
                File openFile = new File(args[0]);
                if (openFile.exists()) {
                    state.open(openFile);
                }
                else {
                    if (openFile.createNewFile()) {
                        state.open(openFile);
                    }
                    else {
                        throw new RuntimeException("Could not create new file");
                    }
                }
            }
            catch (IOException e) {

            }
        }
    }
}
