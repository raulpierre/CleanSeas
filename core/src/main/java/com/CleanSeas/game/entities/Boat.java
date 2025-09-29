package com.CleanSeas.game.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Boat {
    private Texture texture;
    private float x = 100;
    private float y = 300;
    private float vel = 100;

    public Boat(Texture texture){
        this.texture = texture;
    }

    public void update(float delta){
        if(Gdx.input.isKeyPressed(Input.Keys.A)){
            x -= vel * delta;
        }
        else if(Gdx.input.isKeyPressed(Input.Keys.D)){
            x += vel * delta;
        }
        // teste para ver se saiu do limite da tela
        if (x < 0){
            x += texture.getWidth();
        }
        else if( x + texture.getWidth() > Gdx.graphics.getWidth()){
            x = Gdx.graphics.getWidth() - texture.getWidth();
        }
    }

    public float getX(){
        return x;
    }

    public float getY(){
        return y;
    }

    public void draw(SpriteBatch batch){
        batch.draw(texture, x, y);
    }

    public void dispose(){
        texture.dispose();
    }
}
