package com.bravotic.vtlib;

public abstract class VTController {
    protected VT vt;
    protected boolean running;

    public VTController(VT vt) {
        this.vt = vt;
        running = true;
    }

    public abstract void processInput(String input);

    public void run() {
        String input;
        while (running) {
            input = vt.getch();
            processInput(input);
        }
    }
}
