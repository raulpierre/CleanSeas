package com.CleanSeas.game.entities;
import com.badlogic.gdx.math.MathUtils;

public class OceanCurrent {
    private float speedX;
    private float variability;

    public OceanCurrent(float speedX, float variability) {
        this.speedX = speedX;
        this.variability = variability;
    }

    public float getSpeedX() {
        return speedX + MathUtils.random(-variability, variability);
    }

    public void setSpeedX(float speedX) {
        this.speedX = speedX;
    }
}
