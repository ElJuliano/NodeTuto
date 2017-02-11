package com.jmolas.multiplayerdemo.sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Julien on 11/02/2017.
 */

public class SpaceShip extends Sprite {

    private Vector2 previousPosition;

    public SpaceShip(Texture texture) {
        super(texture);
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

    public void flip() {

    }
}
