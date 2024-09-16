package com.bravotic.ate.model;

import com.bravotic.cam.model.CMenu;
import com.bravotic.cam.model.CMenuContainer;
import com.bravotic.cam.model.CMenuContainerListener;
import com.bravotic.cam.model.CMenuItem;

import java.io.*;
import java.sql.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Represents the core of the Text Editor. In general, if you want to interact with the editor, this is the way to do
 * it properly. The AState holds the document we are editing, the menu, and even the prompts being shown. Anything that
 * can't sufficiently be done by any of those is done here. For example, file loading and saving is done in the state,
 * since it makes more sense to keep that separated from the actual logic of editing the file. Likewise, the state also
 * should be used directly when adding menu options for the user. This saves the hassle of having to manually create
 * CMenu entries, and allows plugs to correctly extend the editor without incompatibilities. The state, as the name
 * suggests, also holds information on what is currently being done, or what should be shown on screen. The state keeps
 * track of what view should be displayed to the user, either the MENU, DOCUMENT, or PROMPT.
 * @see com.bravotic.ate.model.ADocument
 * @see com.bravotic.ate.model.APrompt
 * @see com.bravotic.cam.model.CMenuContainer
 * @see com.bravotic.cam.model.CMenu
 */
public class AState implements CMenuContainerListener, APromptChangeListener {
    private final CMenu menuRoot;
    private final CMenuContainer menuContainer;
    private final ADocument document;
    private APrompt prompt;

    private File source;

    /**
     * Keeps track of how many prompts are open. This is because prompts can open other prompts. Since they can appear
     * on top of each other, we need to keep track of how many are open.
     */
    private int promptsOpen;

    private final List<AStateChangeListener> listeners;

    /**
     * Identifier that the document should be the selected view.
     */
    public static final int VIEW_DOCUMENT = 0;

    /**
     * Identifier that the menu should be the selected view.
     */
    public static final int VIEW_MENU = 1;

    /**
     * Identifier that the prompt should be the selected view.
     */
    public static final int VIEW_PROMPT = 2;

    private int viewMode;

    private CMenu findOrAddSubmenu(CMenu menu, String name) {
        for (int i = 0; i < menu.size(); i++) {
            CMenuItem item = menu.get(i);

            if (item.label.equals(name) && item.hasSubmenu) {
                return item.getSubmenu();
            }
            else if (item.label.equals(name)) {
                throw new IllegalStateException("Cannot add a menu option to an item that does not have a submenu");
            }
        }

        CMenu appendedMenu = new CMenu();
        menu.addMenuItem(new CMenuItem(name, appendedMenu));

        return appendedMenu;
    }

    private void notifyFlipped() {
        for(AStateChangeListener l : listeners) {
            l.viewIsFlipped(viewMode);
        }
    }

    private void notifyPromptOpened() {
        for(AStateChangeListener l : listeners) {
            l.promptOpened(prompt);
        }
    }

    private void notifyPromptClosed() {
        for(AStateChangeListener l : listeners) {
            l.promptClosed();
        }
    }

    /**
     * Constructs a new blank state, and all associated models the state manages.
     */
    public AState() {
        menuRoot = new CMenu();
        menuContainer = new CMenuContainer(menuRoot);
        document = new ADocument();
        listeners = new ArrayList<>();
        source = null;

        promptsOpen = 0;

        viewMode = AState.VIEW_DOCUMENT;

        menuContainer.addListener(this);

        prompt = null;

        for (AStateChangeListener l : listeners) {
            l.newFile();
        }
    }

    /**
     * Adds a listener to be notified when the ADocument is changed.
     * @param l The listener to add.
     */
    public void addListener(AStateChangeListener l) {
        listeners.add(l);
    }

    /**
     * Adds a menu option to the root menu. Menu paths come in the form of "dir\subdir". All paths are assumed to be
     * relative to root. Submenus are automatically created when found.
     * @param path The path where the menu item should go.
     * @param item The item to add.
     */
    public void addMenuOption(String path, CMenuItem item) {
        // Split our path into its component pars
        String[] pathParts = path.split("\\\\");

        // Go through each part of the path and find/add the next submenu to move into.
        CMenu currentWorkingMenu = menuRoot;
        for (String part : pathParts) {

            if (!part.isEmpty()) {
                currentWorkingMenu = findOrAddSubmenu(currentWorkingMenu, part);
            }
        }

        currentWorkingMenu.addMenuItem(item);
    }

    /**
     * Get the root menu.
     * @return The root menu.
     */
    public CMenu getMenu() {
        return menuRoot;
    }

    /**
     * Gets the menu container model.
     * @return The menu container model.
     */
    public CMenuContainer getMenuContainer() {
        return menuContainer;
    }

    /**
     * Gets the document model.
     * @return The document model.
     */
    public ADocument getDocument() {
        return document;
    }


    /**
     * Opens a file and sets the document to its contents.
     * @param source The file to open.
     * @throws FileNotFoundException
     */
    public void open(File source) throws FileNotFoundException {
        Scanner sourceReader = new Scanner(new FileReader(source));
        document.bufferClear();

        while (sourceReader.hasNextLine()) {
            String line = sourceReader.nextLine();

            // We do this because not every line is guaranteed to end with a new line character. However, if a line came
            // before, we can assume a line break came between it and the previous line.
            if (!document.isEmpty()) {
                document.bufferInsert("\n");
            }

            document.bufferInsert(line);
        }

        document.move(0, 0);

        this.source = source;

        String fileName = source.getName();
        for (AStateChangeListener l : listeners) {
            l.fileOpened(fileName);
        }
    }

    /**
     * Save the current document to the loaded file. If there is no opened file, nothing will happen.
     * TODO: Make this throw an exception.
     */
    public void save() {
        if (this.source != null) {
            try {
                FileWriter writer = new FileWriter(this.source);
                writer.write(document.toString());
                writer.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Save the file currently opened by the state to a specific other file (not necessarily the one that was read from
     * the disk to load it).
     * @param f The file to save to.
     */
    public void saveAs(File f) {
        this.source = f;
        save();

        String name = f.getName();
        for (AStateChangeListener l : listeners) {
            l.fileOpened(name);
        }
    }

    /**
     * Check if a file is currently being edited, or if the contents of the buffer isn't from a file.
     * @return true if buffer was read from a file, false otherwise.
     */
    public boolean isFileOpened() {
        return this.source != null;
    }

    /**
     * Creates a new document that isn't tied to a file.
     */
    public void newFile() {
        this.source = null;
        document.bufferClear();
        document.move(0, 0);

        for (AStateChangeListener l : listeners) {
            l.newFile();
        }
    }

    /**
     * Toggles between the menu being shown and the document being shown. This was used extensively before the prompt
     * was added, and now should probably be replaced with setView(int viewMode).
     * @deprecated
     */
    public void flip() {
        switch (viewMode) {
            case AState.VIEW_DOCUMENT:
                viewMode = AState.VIEW_MENU;
                break;
            default:
                viewMode = AState.VIEW_DOCUMENT;
        }

        notifyFlipped();
    }

    /**
     * Changes what view is currently being shown to the user.
     * @param viewMode The view to switch to.
     */
    public void setView(int viewMode) {
        this.viewMode = viewMode;
        notifyFlipped();
    }

    /**
     * Gets the currently active view that the window is presenting.
     * @return The view that the window is showing.
     */
    public int getActiveView() {
        return viewMode;
    }

    /**
     * Show a prompt to the user. This will automatically switch the view to the prompt and notify that a prompt was
     * opened.
     * @param p The prompt to be shown to the user.
     */
    public void showPrompt(APrompt p) {
        prompt = p;

        p.addChangeListener(this);
        notifyPromptOpened();

        promptsOpen += 1;
    }

    /**
     * Checks if the menu is open or not.
     * @return True if the menu is open, false if not.
     */
    public boolean isMenuOpen() {
        return viewMode == AState.VIEW_MENU;
    }

    /**
     * Check whether a prompt is open. This is done because technically prompts can layer, a prompt is able to open an
     * additional prompt, and if that additional prompt is closed, it is expected it would fall back to the first
     * prompt.
     * @return true if open, false if not.
     */
    public boolean isPromptOpen() {
        return promptsOpen > 0;
    }


    @Override
    public void submenuOpened(String label, CMenu menu) {}

    @Override
    public void submenuClosed(CMenu menu) {}

    @Override
    public void menuWouldLikeToClose() {
        flip();
    }

    @Override
    public void promptTextChanged(String text) {

    }

    @Override
    public void promptSelectionChanged(int selected) {

    }

    @Override
    public void promptSubmitted() {
        promptsOpen -= 1;
        notifyPromptClosed();
    }
}
