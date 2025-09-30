package com.CleanSeas.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.CleanSeas.game.screens.GameMap;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class CleanSeas extends Game {
    public SpriteBatch batch;
    public AssetManager assets;

    @Override
    public void create() {
        batch = new SpriteBatch();
        assets = new AssetManager();

        assets.load("gamemap.png", com.badlogic.gdx.graphics.Texture.class);
        assets.load("img_testes.png", com.badlogic.gdx.graphics.Texture.class);
        assets.load("anzol_testes.png", com.badlogic.gdx.graphics.Texture.class);
        assets.load("Clownfish.png", com.badlogic.gdx.graphics.Texture.class);
        assets.load("garrafapet.png", com.badlogic.gdx.graphics.Texture.class);

        assets.finishLoading();

        this.setScreen( new com.CleanSeas.game.screens.GameMap(this));
    }
    @Override
    public void dispose(){
        batch.dispose();
        assets.dispose();
    }
}
