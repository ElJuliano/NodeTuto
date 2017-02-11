package com.jmolas.multiplayerdemo.sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.jmolas.multiplayerdemo.entities.Bullet;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Julien on 11/02/2017.
 */

public class SpaceShip extends Sprite {

    private Vector2 previousPosition;

    private List<Bullet> bullets;

    public SpaceShip(Texture texture) {
        super(texture);
        bullets = new ArrayList<Bullet>();
        previousPosition = new Vector2(getX(), getY());
    }

    public SpaceShip(Texture texture, boolean flip) {
        super(texture);
        previousPosition = new Vector2(getX(), getY());
        this.flip(true, true);
    }

    public boolean hasMoved() {
        if(previousPosition.x != getX() || previousPosition.y != getY()) {
            previousPosition.x = getX();
            previousPosition.y = getY();
            return true;
        }
        return false;
    }

    public List<Bullet> getBullets() {
        return bullets;
    }

    public void addBullet() {
        bullets.add(new Bullet(this.getX() + this.getWidth()/2));
    }

    public void flip() {

    }
}
