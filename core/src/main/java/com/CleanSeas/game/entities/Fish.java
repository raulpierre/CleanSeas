package com.CleanSeas.game.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Fish {
    private enum State { Default, Alert, Attack }
    private State state = State.Default;

    private Texture texture;
    private float x, y;
    private float velX, velY;

    // timers
    private float timeDirection;
    private float time;
    private float alertTime;
    private float coolDownAttack = 0f;

    // constantes
    private final float speed = 30f;
    private final float attackSpeed = 220f;

    // para evitar alocar toda hora
    private final Vector2 tmpDir = new Vector2();

    // para inverter horizontalmente
    private boolean facingRight = true;

    //para marcar o hit
    private boolean alreadyHit = false;

    public Fish(Texture texture, float startX, float startY){
        this.texture = texture;
        this.x = startX;
        this.y = startY;
        chooseDirection();
    }

    private void chooseDirection() {
        float angle = MathUtils.random(0f,360f);
        velX = MathUtils.cosDeg(angle) * speed;
        velY = MathUtils.sinDeg(angle) * speed;
        timeDirection = MathUtils.random(1f,3f);
        time = 0f;
    }

    public void update(float delta, OceanCurrent c, Viewport viewport, Vector2 hookPos) {
        time += delta;

        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();

        // bordas horizontais
        if (x < 0) {
            x = 0; velX = Math.abs(velX);
        }
        if (x + texture.getWidth() > worldWidth) {
            x = worldWidth - texture.getWidth(); velX = -Math.abs(velX);
        }

        // bordas verticais (exemplo: fundo = 25, topo = 300)
        if (y < 25) {
            y = 25; velY = Math.abs(velY);
        }
        if (y + texture.getHeight() > 300) {
            y = 300 - texture.getHeight(); velY = -Math.abs(velY);
        }

        float dist = Vector2.dst(x, y, hookPos.x, hookPos.y);

        switch (state) {
            case Default:
                if (time > timeDirection){
                    chooseDirection();
                }
                if (dist < 150f && coolDownAttack <= 0f) {
                    coolDownAttack = 5f;
                    state = State.Alert;
                    alertTime = 1.0f; // tremida
                }
                break;
            case Alert:
                alertTime -= delta;
                x += MathUtils.random(-2f,2f);
                y += MathUtils.random(-2f,2f);

                if (alertTime <= 0f) {
                    // calcula vetor para anzol
                    tmpDir.set(hookPos.x - x, hookPos.y - y).nor();
                    velX = tmpDir.x * attackSpeed;
                    velY = tmpDir.y * attackSpeed;
                    state = State.Attack;
                    resetHit();
                }
                break;
            case Attack:
                coolDownAttack -= delta;
                if (coolDownAttack < 0f) {
                    state = State.Default;
                }
                break;
        }

        // aplica correnteza + movimento
        x += (velX + c.getSpeedX()) * delta;
        y += velY * delta;

        // atualiza direção para desenhar
        if (velX > 0.1f) facingRight = true;
        else if (velX < -0.1f) facingRight = false;
    }

    public void draw(SpriteBatch batch){
        if (facingRight) {
            batch.draw(texture, x, y);
        } else {
            // desenha invertido horizontalmente
            batch.draw(texture, x + texture.getWidth(), y, -texture.getWidth(), texture.getHeight());
        }
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, texture.getWidth(), texture.getHeight());
    }

    public boolean canDamage() {
        return state == State.Attack && !alreadyHit;
    }

    public void markHit() {
        alreadyHit = true;
    }

    public void resetHit() {
        alreadyHit = false;
    }

    /** Não dispose() aqui se usar AssetManager */
    public void dispose(){
        if (texture != null) {
            texture.dispose(); // só se carregou manualmente
        }
    }

    public float getX(){return x;}
    public float getY(){return y;}
}
