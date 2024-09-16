package com.bravotic.ate.swing.view;

import com.bravotic.ate.model.*;
import com.bravotic.ate.swing.controller.AStateController;
import com.bravotic.ate.swing.themes.ATETheme;
import com.bravotic.ate.swing.themes.StandardTheme;
import com.bravotic.cam.swing.view.CMenuContainerView;

import javax.swing.*;
import java.awt.*;

/**
 * A frame which allows us to combine both a document view and a menu view container.
 */
public class AStateView extends JFrame implements AStateChangeListener {
    private final ADocumentView view;
    private final CMenuContainerView menu;
    private final APromptView prompt;

    private final JPanel[] views;

    private final AState state;

    private int activeViewMode;

    private void switchToView(int viewMode) {
       views[activeViewMode].setEnabled(false);
       views[activeViewMode].setVisible(false);

       views[viewMode].setEnabled(true);
       views[viewMode].setVisible(true);
       views[viewMode].requestFocusInWindow();

       activeViewMode = viewMode;
    }

    /**
     * Construct a new frame. Automatically uses the Standard Theme.
     */
    public AStateView(AState state) {
        this(state, new StandardTheme());
    }

    /**
     * Construct a new frame. Allows you to specify a theme.
     * @param theme The theme to use for the document as well as the menus.
     */
    public AStateView(AState state, ATETheme theme) {
        super("[New File]");

        this.state = state;
        activeViewMode = state.getActiveView();

        this.setLayout(null);

        view = new ADocumentView(state.getDocument(), theme);
        menu = new CMenuContainerView(state.getMenuContainer(), theme);
        prompt = new APromptView(theme);

        views = new JPanel[3];

        views[AState.VIEW_DOCUMENT] = view;
        views[AState.VIEW_MENU] = menu;
        views[AState.VIEW_PROMPT] = prompt;

        state.addListener(this);

        AStateController stateController = new AStateController(state);
        setFocusTraversalKeysEnabled(false);

        // Loop through and set up our views
        for (int i = 0; i < views.length; i++) {
            boolean isEnabled = i == activeViewMode;
            add(views[i]);

            views[i].setEnabled(isEnabled);
            views[i].setVisible(isEnabled);
            views[i].setFocusTraversalKeysEnabled(false);

            // Since prompts are fake views, we do not add the ability to interact with the state to them.
            if (i != AState.VIEW_PROMPT) {
                // TODO: These should be the primary listeners, maybe we put this on the Frame?
                views[i].addMouseListener(stateController);
                views[i].addKeyListener(stateController);
            }
        }

        views[activeViewMode].requestFocusInWindow();

        setComponentZOrder(view, 1);
        setComponentZOrder(menu, 0);
        setComponentZOrder(prompt, 0);

        ImageIcon icon = new ImageIcon(getClass().getResource("/com/bravotic/ate/icon.png").getFile());
        setIconImage(icon.getImage());

        PluginLoader pl = new PluginLoader(state, "");
    }

    @Override
    public void validate() {
        super.validate();
        Dimension frameSize = getSize();
        Insets frameBorders = getInsets();
        int contentWidth = frameSize.width - (frameBorders.left + frameBorders.right);
        int contentHeight = frameSize.height - (frameBorders.top + frameBorders.bottom);
        int contentX = frameBorders.left;
        int contentY = frameBorders.top;

        for (JPanel view : views) {
            if (view != null) view.setBounds(contentX, contentY, contentWidth, contentHeight);
        }
    }

    @Override
    public void newFile() {
        this.setTitle("[New File]");
    }

    @Override
    public void fileOpened(String filename) {
        this.setTitle(filename);
    }

    @Override
    public void viewIsFlipped(int viewMode) {
        switchToView(viewMode);
    }

    @Override
    public void promptOpened(APrompt p) {

        // If we aren't already switched to our prompt, switch to it
        if (activeViewMode != AState.VIEW_PROMPT) {
            switchToView(AState.VIEW_PROMPT);
        }

        // Update our prompt material
        prompt.setModel(p);
    }

    @Override
    public void promptClosed() {
        // This active view will NEVER return AState.VIEW_PROMPT, so we can use it to see
        // what is "under" the prompt
        int activeView = state.getActiveView();

        // Make sure we have no more prompts open
        if (!state.isPromptOpen()) {

            // Switch back to whatever is "under" the prompt
            switchToView(activeView);
        }
    }
}
