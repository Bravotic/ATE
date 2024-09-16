package com.bravotic.ate.vtlib;

import com.bravotic.vtlib.VT;

import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;
import java.io.PrintStream;

import static com.bravotic.ate.vtlib.ATextEditorVT100.start;

/**
 * An instance of ATE that is forwarded over a TCP port for debugging.
 */
public class ATextEditorRemoteDebug {
    public static void main(String[] args) throws IOException {
        ServerSocket ss = new ServerSocket(42069);
        System.out.println("Listening on port 42069");
        System.setProperty("line.separator", "\n");

        Socket s = ss.accept();
        start(new VT(s.getInputStream(), new PrintStream(s.getOutputStream())), args);
    }
}
