package com.CleanSeas.game.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.CleanSeas.game.CleanSeas;

/** Launches the desktop (LWJGL3) application. */
public class Lwjgl3Launcher {
    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("CleanSeas");
        //pega a resolucao do monitor e vai fullscreen
        config.setFullscreenMode(Lwjgl3ApplicationConfiguration.getDisplayMode()) ;

        new Lwjgl3Application(new CleanSeas(), config);
    }
}

