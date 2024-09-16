package com.bravotic.ate.model;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.Scanner;

/**
 * Allows the loading of plugins for the ATE editor.
 */
public class PluginLoader {

    /**
     * Creates a new plugin loader and loads plugins into the state.
     * @param state The state to load the plugins into.
     * @param basePackage Unused and deprecated.
     */
    public PluginLoader(AState state, String basePackage) {
        ClassLoader cl = getClass().getClassLoader();
        
        try {
            InputStream in = getClass().getResourceAsStream("/com/bravotic/ate/plugins/plugins.txt");
            Scanner sc = new Scanner(new InputStreamReader(in));
            while (sc.hasNextLine()) {
                Class.forName(sc.nextLine()).getConstructor(AState.class).newInstance(state);
            }
        }
        catch (ClassNotFoundException e) {

        } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
