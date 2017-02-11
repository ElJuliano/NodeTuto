package com.jmolas.multiplayerdemo.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Created by Julien on 11/02/2017.
 */

public class Bullet extends Sprite {

    public static final int SPEED = 500;
    public static final int DEFAULT_Y = 80;
    private static Texture texture;

    float x,y;

    public boolean remove = false;

    public Bullet(float x) {
        this.x = x;
        this.y = DEFAULT_Y;

        if(texture == null) {
            texture = new Texture("bullet.png");
        }
    }

    public void update(float dt) {
        y += SPEED * dt;
        if(y > Gdx.graphics.getHeight()){
            remove = true;
        }

    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, x, y);
    }
}
