package com.cmov.bomberman.model;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class BombermanDrawing extends Drawing {
	private static final int SPRITE_LINE = 0;
	private static final int SPRITE_COLUMN = 0;
	private static final int MAX_MOVEMENT_STEP = 3;
	private static final int MAX_DIE_STEP = 6;

	private static Bitmap[] spriteTop;
	private static Bitmap[] spriteBottom;
	private static Bitmap[] spriteLeft;
	private static Bitmap[] spriteRight;
	private static Bitmap[] spriteDestroy;

	private int step;
	private String currentAction;

	public BombermanDrawing(final Position position, final int step, final String currentAction) {
		super(position);
		this.step = step;
		this.currentAction = currentAction;

		if (spriteTop == null) {
			spriteTop = GameUtils.readCharacterSprite(SPRITE_LINE + 1, SPRITE_COLUMN, MAX_MOVEMENT_STEP);
		}
		if (spriteBottom == null) {
			spriteBottom = GameUtils.readCharacterSprite(SPRITE_LINE, SPRITE_COLUMN, MAX_MOVEMENT_STEP);
		}
		if (spriteLeft == null) {
			spriteLeft = GameUtils
					.readCharacterSprite(SPRITE_LINE + 1, SPRITE_COLUMN + MAX_MOVEMENT_STEP, MAX_MOVEMENT_STEP);
		}
		if (spriteRight == null) {
			spriteRight = GameUtils
					.readCharacterSprite(SPRITE_LINE, SPRITE_COLUMN + MAX_MOVEMENT_STEP, MAX_MOVEMENT_STEP);
		}
		if (spriteDestroy == null) {
			spriteDestroy = GameUtils.readCharacterSprite(SPRITE_LINE, SPRITE_COLUMN + MAX_DIE_STEP, MAX_DIE_STEP);
		}
	}


	@Override
	public void draw(Canvas canvas) {
		if (currentAction.equals(MovableAgentActions.MOVE_TOP.toString())) {
			canvas.drawBitmap(spriteTop[step], getPosition().getX(), getPosition().getY(), null);
		} else if (currentAction.equals(MovableAgentActions.MOVE_BOTTOM.toString())) {
			canvas.drawBitmap(spriteBottom[step], getPosition().getX(), getPosition().getY(), null);
		} else if (currentAction.equals(MovableAgentActions.MOVE_LEFT.toString())) {
			canvas.drawBitmap(spriteLeft[step], getPosition().getX(), getPosition().getY(), null);
		} else if (currentAction.equals(MovableAgentActions.MOVE_RIGHT.toString())) {
			canvas.drawBitmap(spriteRight[step], getPosition().getX(), getPosition().getY(), null);
		} else if (currentAction.equals(AgentActions.DESTROY.toString())) {
			canvas.drawBitmap(spriteDestroy[step], getPosition().getX(), getPosition().getY(), null);
		}
	}
}
