package com.CleanSeas.game.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Fish {

    private enum State { DEFAULT, ALERT, ATTACK }
    private State state = State.DEFAULT;

    private Texture texture;
    private float x, y;
    private float velX, velY;
    private float scale = 2f;
    private float rotationAngle = 0f;
    private boolean facingRight = true;

    private float timeDirection = 0f;
    private float time = 0f;
    private float alertTime = 0f;
    private float coolDownAttack = 0f;

    private final float speed = 30f;
    private final float attackSpeed = 220f;
    private final float perceptionRange = 150f;

    private Vector2 toHook = new Vector2();
    private boolean alreadyHit = false;

    public Fish(Texture texture, float startX, float startY){
        this.texture = texture;
        this.x = startX;
        this.y = startY;
        chooseDirection();
    }

    private void chooseDirection() {
        float angle = MathUtils.random(0f, 360f);
        velX = MathUtils.cosDeg(angle) * speed;
        velY = MathUtils.sinDeg(angle) * speed;
        timeDirection = MathUtils.random(2f, 4f);
        time = 0f;
    }

    public void update(float delta, Viewport viewport, Vector2 hookPos){
        time += delta;
        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();

        // bordas
        if(x < 0){ x = 0; velX = Math.abs(velX); }
        if(x + texture.getWidth() > worldWidth){ x = worldWidth - texture.getWidth(); velX = -Math.abs(velX); }
        if(y < 25){ y = 25; velY = Math.abs(velY); }
        if(y + texture.getHeight() > 450){ y = 450 - texture.getHeight(); velY = -Math.abs(velY); }

        switch(state){
            case DEFAULT:
                if(time > timeDirection){
                    chooseDirection();
                }
                updateRotationDefault();
                if(isHookInSight(hookPos) && coolDownAttack <= 0f){
                    coolDownAttack = 5f;
                    state = State.ALERT;
                    alertTime = 1.5f;
                }
                break;

            case ALERT:
                alertTime -= delta;
                updateRotationToHook(hookPos);
                if(alertTime <= 0f){
                    state = State.ATTACK;
                    toHook.set(hookPos.x - (x + texture.getWidth()/2f),
                        hookPos.y - (y + texture.getHeight()/2f)).nor();
                    velX = toHook.x * attackSpeed;
                    velY = toHook.y * attackSpeed;
                    resetHit();
                }
                break;

            case ATTACK:
                coolDownAttack -= delta;
                updateRotationDefault();
                if(coolDownAttack < 0f){
                    state = State.DEFAULT;
                    resetHit();
                }
                break;
        }

        // <<< CORREÇÃO 3: Lógica para determinar a direção (esquerda/direita)
        // Agora, a direção é atualizada de acordo com o estado do peixe.
        if (state == State.ALERT) {
            // No alerta, ele encara o anzol
            facingRight = hookPos.x > (x + texture.getWidth() / 2f);
        } else {
            // Nos outros estados, a direção é baseada na velocidade
            if (Math.abs(velX) > 0.1f) { // Adicionado um pequeno limiar para evitar viradas indesejadas
                facingRight = velX > 0f;
            }
        }


        x += velX * delta;
        y += velY * delta;
    }

    private void updateRotationDefault(){
        float targetAngle = new Vector2(velX, velY).angleDeg();
        updateRotation(targetAngle, 2f);
    }

    private void updateRotationToHook(Vector2 hookPos){
        toHook.set(hookPos.x - (x + texture.getWidth()/2f), hookPos.y - (y + texture.getHeight()/2f));
        float targetAngle = toHook.angleDeg();
        updateRotation(targetAngle, 5f);
    }

    /**
     * <<< CORREÇÃO 4: Novo método central para calcular a rotação.
     * Este método garante que o peixe nunca fique de cabeça para baixo.
     */
    private void updateRotation(float targetAngle, float lerpFactor) {
        float effectiveAngle = targetAngle;

        // Se o peixe está virado para a esquerda, normalizamos o ângulo
        // como se ele estivesse virado para a direita.
        // Ex: um alvo a 150 graus (cima-esquerda) se torna 30 graus (cima-direita)
        if (!facingRight) {
            effectiveAngle = 180 - targetAngle;
        }

        // Prendemos (clamp) o ângulo entre -45 e 45 graus para evitar
        // que ele incline demais e vire de ponta cabeça.
        float clampedAngle = MathUtils.clamp(effectiveAngle, -45f, 45f);

        // Suaviza a transição da rotação atual para a nova rotação limitada
        this.rotationAngle = MathUtils.lerpAngleDeg(this.rotationAngle, clampedAngle, lerpFactor * Gdx.graphics.getDeltaTime());
    }


    private boolean isHookInSight(Vector2 hookPos){
        toHook.set(hookPos.x - (x + texture.getWidth()/2f), hookPos.y - (y + texture.getHeight()/2f));
        return toHook.len() <= perceptionRange;
    }

    /**
     * <<< CORREÇÃO 5: O método draw agora usa a variável 'facingRight' para virar
     * a imagem horizontalmente (flip), e 'rotationAngle' para a inclinação.
     */
    public void draw(SpriteBatch batch){
        batch.draw(texture,
            x, y,
            texture.getWidth() / 2f, texture.getHeight() / 2f,
            texture.getWidth() * scale, texture.getHeight() * scale,
            1f, 1f,
            rotationAngle,
            0, 0,
            texture.getWidth(), texture.getHeight(),
            !facingRight, false); // O parâmetro 'flipX' é definido por '!facingRight'
    }

    public Rectangle getBounds(){
        return new Rectangle(x, y, texture.getWidth(), texture.getHeight());
    }

    public boolean canDamage(){ return state == State.ATTACK && !alreadyHit; }
    public void markHit(){ alreadyHit = true; }
    public void resetHit(){ alreadyHit = false; }
}
