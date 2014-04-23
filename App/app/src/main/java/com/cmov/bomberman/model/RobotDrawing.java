package com.cmov.bomberman.model;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class RobotDrawing extends Drawing {
    private static final int SPRITE_LINE = 2;
    private static final int SPRITE_MOVE_COLUMN = 0;
    private static final int SPRITE_DIE_COLUMN = 6;
    private static final int MAX_MOVEMENT_STEP = 1;
    private static final int MAX_DIE_STEP = 5;

    private static Bitmap[] spriteMove;
    private static Bitmap[] spriteDie;

    private int step;
    private String currentAction;

    public RobotDrawing(final Position position, final int step, final String currentAction) {

        super(position);
        this.step = step;
        this.currentAction = currentAction;

        if (spriteMove == null) {
            spriteMove = GameUtils.readCharacterSprite(SPRITE_LINE, SPRITE_MOVE_COLUMN, MAX_MOVEMENT_STEP);
        }
        if (spriteDie == null) {
            spriteDie = GameUtils.readCharacterSprite(SPRITE_LINE, SPRITE_DIE_COLUMN, MAX_DIE_STEP);
        }
    }

    @Override
    public void draw(Canvas canvas) {
		if (currentAction.equals(AgentActions.DESTROY.toString())) {
			canvas.drawBitmap(spriteDie[step], getPosition().getX(), getPosition().getY(), null);
        } else {
            canvas.drawBitmap(spriteMove[step], getPosition().getX(), getPosition().getY(), null);
        }
    }
}
