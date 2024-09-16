package com.bravotic.ate.vtlib;

import com.bravotic.ate.model.*;
import com.bravotic.ate.vtlib.controller.AStateController;
import com.bravotic.ate.vtlib.view.AStateView;
import com.bravotic.vtlib.VT;

import java.io.File;
import java.io.IOException;

/**
 * A version of ATE that runs in a terminal using VTlib.
 */
public class ATextEditorVT100 {

    /**
     * Starts the actual VT100 ATE version. This is done so that the remote debug launcher can reuse this same code.
     * @param vt The VT100 object.
     * @param args Command line arguments.
     * @throws IOException
     */
    static void start(VT vt, String[] args) throws IOException {
        vt.eraseScreen();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            vt.scrollScreen();
            vt.cursorHome(vt.screenSize);
            vt.out.println();
        }));

        AState state = new AState();
        PluginLoader pl = new PluginLoader(state, "");

        // TODO: Ideally this should go below the view, but that causes some issues, so for now it goes up here.
        if (args.length != 0) {
            state.open(new File(args[0]));
        }

        AStateController controller = new AStateController(state, vt);
        AStateView view = new AStateView(state, vt);

        controller.run();

        vt.getch();
    }
    public static void main(String[] args) throws IOException {
        start(new VT(System.in, System.out), args);
    }
}
