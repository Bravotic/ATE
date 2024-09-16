package com.bravotic.ate.model;

import com.bravotic.cam.model.CMenu;
import com.bravotic.cam.model.CMenuAction;
import com.bravotic.cam.model.CMenuItem;
import org.w3c.dom.Text;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class CFileSelectMenu extends CMenu implements CMenuAction, TextPromptAction {

    protected File dir;
    protected final AState state;
    protected final APrompt newFilePrompt;

    protected final CFileSelectMenuActions actions;

    // TODO: This needs a major rewrite
    public CFileSelectMenu(AState state, String path, CFileSelectMenuActions actions) {
        dir = new File(path);
        this.state = state;
        newFilePrompt = new APrompt("Please enter a file name: ", APrompt.QUESTION, null, this);
        this.actions = actions;

        File[] dirArray = dir.listFiles();

        this.allItems.add(new CMenuItem("[New File]", item -> {
            state.showPrompt(newFilePrompt);

            return true;
        }));

        CMenu dummyMenu = new CMenu();
        if (dirArray != null) {
            for (File f : dirArray) {
                CMenuItem item;

                if (f.isDirectory()) {
                    item = new CMenuItem(f.getName() + "/", dummyMenu);
                }
                else {
                    item = new CMenuItem(f.getName(), (CMenuAction) this);
                }

                this.allItems.add(item);
            }
        }

        if (this.allItems.size() > 1) {
            selected = 1;
        }
    }

    public void select() {
        CMenuItem sel;

        sel = this.activeItems.get(selected);


        // TODO: Change this to use refresh()
        if (sel.hasSubmenu) {
            sel.setSubmenu(new CFileSelectMenu(state, dir.getPath() + File.separator + this.activeItems.get(selected).label, actions));
            this.notifyItemSelected(sel, true);
        }
        else {
            super.select();
        }

    }

    @Override
    public boolean menuItemSelected(CMenuItem item) {
        File source = new File(dir.getPath(), item.label);
        return actions.fileSelected(source);
    }

    @Override
    public void submitted(String text) {
        File source = new File(dir.getPath(), text);

        try {
            source.createNewFile();
            actions.fileSelected(source);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void closed() {
        state.setView(AState.VIEW_MENU);
    }
}
