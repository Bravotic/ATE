package com.bravotic.ate.plugins.standardoptions;

import com.bravotic.ate.model.*;
import com.bravotic.cam.model.CMenu;
import com.bravotic.cam.model.CMenuAction;
import com.bravotic.cam.model.CMenuItem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class StandardFileOptions implements CMenuAction {
    private final AState state;

    private boolean newFile() {
        // TODO: Replace with "isFileModified"
        if (state.isFileOpened()) {
            state.showPrompt(new ConfirmActionPrompt(state, "Are you sure you'd like to create a new file?", () -> {
                state.newFile();
                state.setView(AState.VIEW_DOCUMENT);
            }));

            return true;
        }
        else {
            state.newFile();
            return false;
        }
    }

    private boolean openFile(File f) {
        if (state.isFileOpened()) {
            state.showPrompt(new ConfirmActionPrompt(state,
                    "Are you sure you'd like to open a this file?",
                    () -> {
                        try {
                            state.open(f);
                            state.setView(AState.VIEW_DOCUMENT);
                        } catch (FileNotFoundException e) {
                            state.showPrompt(new ExceptionPrompt(state, e));
                        }
            }));

            return true;
        }
        else {
            try {
                state.open(f);
                return false;
            }
            catch (IOException e) {
                state.showPrompt(new ExceptionPrompt(state, e));
                return true;
            }
        }
    }

    private boolean saveFile() {
        if (state.isFileOpened()) {
            state.save();
            return false;
        }
        else {
            state.showPrompt(new AOkPrompt("No file is open, use Save As to create a new file", APrompt.WARNING));
            return true;
        }
    }

    private boolean saveFileAs(File f) {
        // If the file has content, we verify if the user wants to overwrite it.
        if (f.length() != 0) {
            state.showPrompt(new ConfirmActionPrompt(state, "Are you sure you'd like to overwrite this file's contents?", () -> {
                state.saveAs(f);
                state.setView(AState.VIEW_DOCUMENT);
            }));
            return true;
        }
        else {
            state.saveAs(f);
            return false;
        }
    }

    @Override
    public boolean menuItemSelected(CMenuItem item) {
        switch (item.label) {
            case "New":
                return newFile();
            case "Save":
                return saveFile();
            default:
                return false;
        }
    }

    public StandardFileOptions(AState state) {
        this.state = state;

        state.addMenuOption("File", new CMenuItem("New", this));
        state.addMenuOption("File", new CMenuItem("Open", (CMenu)new CFileSelectMenu(state, ".", this::openFile)));
        state.addMenuOption("File", new CMenuItem("Save", this));
        state.addMenuOption("File", new CMenuItem("Save As", (CMenu)new CFileSelectMenu(state, ".", this::saveFileAs)));

    }
}
