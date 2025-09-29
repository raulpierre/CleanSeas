package com.CleanSeas.game.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Hook {
    public Texture texture;
    private float x;
    private float y;
    private float xboat;
    private float posicaoBaseY;
    private float velLateral = 300;//velocidaed desvio do anzol
    private float width;
    private float height;
    private float velY = 30;//para calcular a gravidade
    private float g;//gravidade

    public Hook(Texture texture, float startX, float startY){
        this.texture = texture;
        this.x = startX;
        this.y = startY;
        this.width = texture.getWidth();
        this.height = texture.getHeight();
    }

    public void update(float delta, float Xboat, float YBoat) {
        float limEsq = Xboat - 20;//limite desvio para esquerda
        float limDir = Xboat + 20;//limite de desvio para a direita

        //relacionado aos movimentos no eixo Y
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            if(y > YBoat - 20){
                y = YBoat - 20;
            }
            else{
                y += velY * 2.0f * delta;
            }
        }
        else {
            if (y < 25) {
                g = 0;
            } else {
                g = velY * delta;
            }

            y -= g;
        }

        //Calcula o movimento de desvio do anzol
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            if (x <= limEsq){
                x = limEsq;
            }
            else {
                x -= velLateral * delta;
            }
        }
        else if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
            if (x >= limDir){
                x = limDir;
            }
            else {
                x += velLateral * delta;
            }
        }
        else {
            if (x < Xboat) {
                x += velLateral * delta;
                if (x > Xboat) {
                    x = Xboat;
                }
            }
            else if (x > Xboat) {
                x -= velLateral * delta;
                if (x < Xboat) {
                    x = Xboat;
                }
            }
        }
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, texture.getWidth(), texture.getHeight());
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void draw(SpriteBatch batch) {
        batch.draw(texture, x, y);
    }

    public void dispose(){
        texture.dispose();
    }
}
