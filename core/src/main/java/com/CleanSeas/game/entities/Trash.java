package com.CleanSeas.game.entities;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Pool.Poolable;

public class Trash implements Poolable{
    public enum Depth {
        surface(10), middle(20), deep(40);
        public final int points;
        Depth(int points) { this.points = points; }
    }
    public Texture texture;
    private float x;
    private float y;
    private float scale = 1.5f;
    public Depth depth;

    public Trash(){
    }

    public void init(Texture texture, float x, float y, int camada){
            this.texture =  texture;
            this.x = x;
            this.y = y;

            switch(camada) {
                case 0 :
                    depth = Depth.surface;
                    break;
                case 1 :
                    depth = Depth.middle;
                    break;
                default :
                    depth = Depth.deep;
                    break;
            }
    }

    public void update(float delta, OceanCurrent C) {
        //y -= * delta;
        x += C.getSpeedX() * delta;
    }

    public void draw(SpriteBatch batch) {
        float width = texture.getWidth() * scale;
        float height = texture.getHeight() * scale;

        batch.draw(texture, x, y, width, height);
    }

    public boolean ScreenOut(float ScreenWidth, float ScreenHeight){
        return (x < -texture.getWidth() || x > ScreenWidth + texture.getWidth() || y < -texture.getHeight() || y > ScreenHeight + texture.getHeight());
    }

    @Override
    public void reset() {
        //Pool chama o reset quando o objeto e devolvido
        x = y = 0;
        texture = null;
        depth = null;
        }

    public Rectangle getBounds() {
        return new Rectangle(x, y, texture.getWidth(), texture.getHeight());
    }

    public void dispose() {
        texture.dispose();
    }
}
