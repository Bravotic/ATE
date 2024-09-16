package com.bravotic.vtlib;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

/**
 * Represents a VT100 terminal. When using a terminal, use exclusively this class to echo to the terminal.
 */
public class VT {
    /**
     * The stdin stream from the terminal. Use this instead of System.in.
     */
    public final InputStream in;

    /**
     * The stdout to the terminal. Use this instead of System.out.
     */
    public final PrintStream out;

    /**
     * The size of the screen. This reports the furthest position the cursor was able to travel.
     */
    public final Position screenSize;

    public VT(InputStream in, PrintStream out) {
        // Set our input and output streams
        this.in = in;
        this.out = out;

        // Make sure our terminal is okay (also checks that it is vt100 compliant)
        if(!queryDeviceStatus()) {
            throw new IllegalStateException("Terminal is not OK, or is not a VT100");
        }

        // Simulate xterm style alternate buffer mode by erasing the screen.
        eraseScreen();

        // Move the cursor so far it goes off the screen
        cursorHome(5000, 5000);

        // See where our cursor ended up, it will be the lower right hand corner of our screen.
        screenSize = queryCursorPosition();

        // Return the cursor to its home
        cursorHome();
    }

    public String getch() {
        // TODO: The way we are handling escape sequences right now (assuming that they all come on a burst to stdin) is
        // fine for most terminal emulators, but does NOT work on low baud rate physical terminals. This should be
        // reworked to look for the known vt100 sequences and not stop reading until the full sequence is found.
        StringBuilder ret = new StringBuilder();
        try {
            do {
                int ch = in.read();
                if (ch == -1) {
                    throw new RuntimeException("EOF Reached");
                }
                else {
                    ret.append((char)ch);
                }
            } while (in.available() > 0);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        return ret.toString();
    }

    public boolean queryDeviceStatus() {
        out.print("\033[5n");
        String response = getch();

        return response.equals("\033[0n");
    }

    public Position queryCursorPosition() {
        out.print("\033[6n");
        String response = getch();
        if (response.startsWith("\033[")) {
            String row = response.substring(2, response.indexOf(';'));
            String col = response.substring(response.indexOf(';') + 1, response.length() - 1);

            return new Position(Integer.parseInt(row), Integer.parseInt(col));
        }
        else {
            return null;
        }
    }

    public void cursorHome() {
        out.print("\033[H");
    }

    public void cursorHome(Position pos) {
        cursorHome(pos.row, pos.column);
    }

    public void cursorHome(int row, int column) {
        out.print("\033[" + row + ";" + column + "H");
    }

    public void cursorUp() {
        out.print("\033[A");
    }

    public void cursorUp(int count) {
        out.print("\033[" + count + "A");
    }

    public void cursorDown() {
        out.print("\033[B");
    }

    public void cursorDown(int count) {
        out.print("\033[" + count + "B");
    }

    public void cursorForward() {
        out.print("\033[C");
    }

    public void cursorForward(int count) {
        out.print("\033[" + count + "C");
    }

    public void cursorBackward() {
        out.print("\033[D");
    }

    public void cursorBackward(int count) {
        out.print("\033[" + count + "D");
    }

    public void scrollScreen() {
        out.print("\033[r");
    }

    public void scrollScreen(int start, int end) {
        out.print("\033[" + start + ";" + end + "r");
    }

    public void scrollDown() {
        out.print("\033D");
    }

    public void scrollUp() {
        out.print("\033M");
    }

    public void eraseEndOfLine() {
        out.print("\033[K");
    }

    public void eraseStartOfLine() {
        out.print("\033[2k");
    }

    public void eraseLine() {
        out.print("\033[2K");
    }

    public void eraseDown() {
        out.print("\033[J");
    }

    public void eraseUp() {
        out.print("\033[1J");
    }

    public void eraseScreen() {
        out.print("\033[2J");
    }

    public void setAttributeMode(int... attrs) {
        if (attrs.length > 0) {
            out.print("\033[" + attrs[0]);
            for (int i = 1; i < attrs.length; i++) {
                out.print(";" + attrs[i]);
            }
            out.print("m");
        }

    }
}
