package com.cmov.bomberman.model;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Created by JoãoEduardo on 17/04/2014.
 */
public class Wall implements Drawable{

    public static Bitmap[] sprite;
    private static final int SPRITE_LINE = 13;
    private static final int SPRITE_COLUMN = 0;
    private static final int NUM_IMAGES = 1;

    private Position position;

    public Wall(Position position){

        this.position = position;

        if(sprite == null)
        sprite = GameUtils.readCharacterSprite(SPRITE_LINE, SPRITE_COLUMN, NUM_IMAGES);
    }

    public Position getPosition() { return position; }

    @Override
    public void draw(final Canvas canvas) {
        canvas.drawBitmap(sprite[0], getPosition().getX(), getPosition().getY(), null);
    }
}
