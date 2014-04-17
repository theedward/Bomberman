package com.cmov.bomberman.model;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Bomberman extends MovableAgent {
    public enum Actions {
        MOVE_TOP, MOVE_BOTTOM, MOVE_LEFT, MOVE_RIGHT, PUT_BOMB, DIE
    };


    private static Bitmap[] spriteTop;
    private static Bitmap[] spriteBottom;
    private static Bitmap[] spriteLeft;
    private static Bitmap[] spriteRight;
    private static Bitmap[] spriteDie;

    private static final int SPRITE_LINE = 0;
    private static final int SPRITE_COLUMN = 0;
    private static final int MAX_MOVEMENT_STEP = 3;
    private static final int MAX_DIE_STEP = 6;
    private static final int IMAGE_WIDTH = 10;
    private static final int IMAGE_HEIGHT = 10;

    int explosionRange;
    int step;
    String currentAction;
    boolean isDead;
    boolean isDestroyed;

    public Bomberman(Position pos, Algorithm ai, int range, int speed) {
        super(pos,ai,speed);
        explosionRange = range;
    }

    private boolean isDead(){
        return isDead;
    }

    private Move ActionToMove(String action) {

        if(action.equals(Actions.MOVE_BOTTOM)) {
            return Move.DOWN;
        } else if (action.equals(Actions.MOVE_TOP)) {
            return Move.UP;
        } else if (action.equals(Actions.MOVE_LEFT)) {
            return Move.LEFT;
        } if (action.equals(Actions.MOVE_RIGHT)) {
            return Move.RIGHT;
        } else return null;
    }

    @Override
    public void draw(Canvas canvas) {

    }

    @Override
    public boolean isDestroyed() {
        return isDestroyed;
    }

    @Override
    public void play(State state) {

        String nextAction = getAlgorithm().getNextActionName();

        Move move = ActionToMove(nextAction);
        String collision = move(state, move).toString();

        if (collision != null) {
            if (collision.equals(Collision.WCHARACTER.toString())) {
                step = 0;
                isDead = true;
            }
       }

        if (nextAction.equals(Actions.PUT_BOMB.toString())) {
            //state.addCharacter(new Bomb());
        } else if (isDead()) {
            if (step > 0 && step < MAX_DIE_STEP) {
                step = (step+1) % 6;    // when does it have do be removed from playable list in state
            } else if (step == MAX_DIE_STEP) {
                isDestroyed = true;
                return;
            }
        } else if(! currentAction.equals(nextAction)) {
            currentAction = nextAction;
            step = 0;
        } else  if (step > 0 && step < MAX_MOVEMENT_STEP) {
            step = (step+1) % 3;
        }

    }
}
