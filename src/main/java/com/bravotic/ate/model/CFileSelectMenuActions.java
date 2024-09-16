package com.bravotic.ate.model;

import java.io.File;

/**
 * Represents actions on a CFileSelectMenu.
 */
public interface CFileSelectMenuActions {
    /**
     * Invoked when a file is selected.
     * @param f The file that was selected.
     */
    boolean fileSelected(File f);
}
