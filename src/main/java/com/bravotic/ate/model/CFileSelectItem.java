package com.bravotic.ate.model;

import com.bravotic.cam.model.CMenuAction;
import com.bravotic.cam.model.CMenuItem;
import java.io.File;

/**
 * Represents an item in a file select menu.
 */
public class CFileSelectItem extends CMenuItem {
    private final File file;

    /**
     * Constructs a new File select item representing a specific File item.
     * @param file The File this option represents.
     * @param label The label of this option.
     * @param action The action to be taken when submitted.
     */
    public CFileSelectItem(File file, String label, CMenuAction action) {
        super(label, action);
        this.file = file;
    }

    /**
     * Get the file associated with this menu option.
     * @return The File associated with this menu item.
     */
    public File getFile() {
        return file;
    }
}
