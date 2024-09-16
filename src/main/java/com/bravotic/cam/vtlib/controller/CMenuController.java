package com.bravotic.cam.vtlib.controller;
import com.bravotic.cam.model.CMenu;
import com.bravotic.vtlib.VT;
import com.bravotic.vtlib.VTController;

import java.util.Arrays;

public class CMenuController extends VTController {
    private CMenu model;

    private String searchTerm;

    public void processInput(String input) {
        switch (input) {
            case "\011":
            case "\033[B":
                model.selectNext();
                break;
            case "\033\011":
            case "\033[Z":
            case "\033[A":
                model.selectLast();
                break;
            case "\012":
            case "\033[C":
                model.select();
                break;
            case "\033[D":
                model.back();
                break;
        }

        if (input.charAt(0) != '\033') {
            // TODO: This may or may not work
            for (char ch : input.toCharArray()) {
                if (ch >= ' ' && ch < 127) {
                    searchTerm += ch;
                    model.setSearchFilter(searchTerm);
                } else if (ch == 127 || ch == 8) {
                    if (searchTerm.isEmpty()) {
                        model.setSearchFilter("");
                        model.back();
                    } else {
                        searchTerm = searchTerm.substring(0, searchTerm.length() - 1);
                        model.setSearchFilter(searchTerm);
                    }
                }
            }
        }
    }

    public CMenuController(CMenu model, VT vt) {
        super(vt);
        searchTerm = model.getSearchTerm();
        this.model = model;
    }

    public void setModel(CMenu model) {
        this.model = model;
        this.searchTerm = model.getSearchTerm();
    }
}
