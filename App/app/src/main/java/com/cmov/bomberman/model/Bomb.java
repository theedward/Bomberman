package com.cmov.bomberman.model;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Created by JoãoEduardo on 17/04/2014.
 */
public class Bomb extends Agent {

    public enum Actions {
        DESTROY
    }

    private static Bitmap bombSprite[];
    private static Bitmap explosionSprite[];
    private static final int BOMB_SPRITE_LINE = 1;
    private static final int BOMB_SPRITE_COLUMN = 6;
    private static final int BOMB_NUM_IMAGES = 3;
    private static final int EXPL_SPRITE_LINE = 11;
    private static final int EXPL_SPRITE_COLUMN = 0;
    private static final int EXPL_NUM_IMAGES = 4;
    private static final int BOMB_MAX_STEP = 3;
    private static final int EXPL_MAX_STEP = 4;

    private int bombRange;

    private int bombStep;
    private int explStep;

    //At the moment all bombs explode after 5 seconds, this can be passed to a constructor or changed
    private int timeout = 5;

    private boolean destroyed;



    public Bomb(final Position startingPos, int bombRange, Algorithm ai){
        super(startingPos, ai);

        this.bombRange = bombRange;

        if(bombSprite == null)
            bombSprite = GameUtils.readCharacterSprite(BOMB_SPRITE_LINE,BOMB_SPRITE_COLUMN,BOMB_NUM_IMAGES);

        if(explosionSprite == null)
            explosionSprite = GameUtils.readCharacterSprite(EXPL_SPRITE_LINE,EXPL_SPRITE_COLUMN,EXPL_NUM_IMAGES);
    }

    @Override
    public void draw(final Canvas canvas) {
        if(timeout != 0)
          canvas.drawBitmap(bombSprite[bombStep],getCurrentPos().getX(), getCurrentPos().getY(),null);
        else
          canvas.drawBitmap(explosionSprite[explStep], getCurrentPos().getX(), getCurrentPos().getY(), null);
    }

    @Override
    public void play(State state) {
        if (bombStep > 0 && bombStep < BOMB_MAX_STEP) {
            bombStep++;
        } else if (explStep == EXPL_MAX_STEP) {
            state.bombExplosion(bombRange, this);
            destroyed = true;
            return;
        } else if (bombStep == BOMB_MAX_STEP) {
            timeout = 0;
            explStep++;
        }
    }

    @Override
    public boolean isDestroyed() {
        return false;
    }
}
