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
        // arguments must be changed
        if (spriteTop == null) {
            spriteTop = GameUtils.readCharacterSprite(SPRITE_LINE+1, SPRITE_COLUMN, MAX_MOVEMENT_STEP);
        }
        if (spriteBottom == null) {
            spriteBottom = GameUtils.readCharacterSprite(SPRITE_LINE, SPRITE_COLUMN, MAX_MOVEMENT_STEP);
        }
        if (spriteLeft == null) {
            spriteLeft = GameUtils.readCharacterSprite(SPRITE_LINE+1, SPRITE_COLUMN+MAX_MOVEMENT_STEP, MAX_MOVEMENT_STEP);
        }
        if (spriteRight == null) {
            spriteRight = GameUtils.readCharacterSprite(SPRITE_LINE, SPRITE_COLUMN+MAX_MOVEMENT_STEP, MAX_MOVEMENT_STEP);
        }
        if (spriteDie == null) {
            spriteDie = GameUtils.readCharacterSprite(SPRITE_LINE, SPRITE_COLUMN+MAX_DIE_STEP, MAX_DIE_STEP);
        }
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
        if (currentAction.equals(Actions.MOVE_TOP.toString())) {
            canvas.drawBitmap(spriteTop[step], getCurrentPos().getX(), getCurrentPos().getY(), null);
        } else if (currentAction.equals(Actions.MOVE_BOTTOM.toString())) {
            canvas.drawBitmap(spriteBottom[step], getCurrentPos().getX(), getCurrentPos().getY(), null);
        } else if (currentAction.equals(Actions.MOVE_LEFT.toString())) {
            canvas.drawBitmap(spriteLeft[step], getCurrentPos().getX(), getCurrentPos().getY(), null);
        } else if (currentAction.equals(Actions.MOVE_RIGHT.toString())) {
            canvas.drawBitmap(spriteRight[step], getCurrentPos().getX(), getCurrentPos().getY(), null);
        } else if (currentAction.equals(Actions.DIE.toString())) {
            canvas.drawBitmap(spriteDie[step], getCurrentPos().getX(), getCurrentPos().getY(), null);
        }
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
                currentAction = Actions.DIE.toString();
                step = 0;
                isDead = true;
            }
        } else if (nextAction.equals(Actions.PUT_BOMB.toString())) {
            state.addCharacter(new Bomb(this.getCurrentPos(),explosionRange, null));
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
