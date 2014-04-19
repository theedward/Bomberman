package com.cmov.bomberman.model;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Created by JoãoEduardo on 17/04/2014.
 */
public class Robot extends MovableAgent {


	private static final int SPRITE_LINE = 2;

	;
	private static final int SPRITE_COLUMN = 0;
	private static final int SPRITE_DIE_LINE = 2;
	private static final int SPRITE_DIE_COLUMN = 6;
	private static final int MAX_MOVEMENT_STEP = 6;
	private static final int MAX_DIE_STEP = 5;
	private static final int IMAGE_WIDTH = 10;
	private static final int IMAGE_HEIGHT = 10;
	private static Bitmap[] sprite;
	private static Bitmap[] spriteDie;
	private boolean isDestroyed;
	private String currentAction;
	private int moveStep;
	private int dieStep;

	public Robot(final int id, final Position position, final Algorithm ai, final int speed) {
		super(id, position, ai, speed);

		sprite = GameUtils.readCharacterSprite(SPRITE_LINE, SPRITE_COLUMN, MAX_MOVEMENT_STEP);
		spriteDie = GameUtils.readCharacterSprite(SPRITE_DIE_LINE, SPRITE_DIE_COLUMN, MAX_DIE_STEP);
	}

	@Override
	public void draw(Canvas canvas) {
		if (currentAction.equals(Actions.MOVE)) {
			canvas.drawBitmap(sprite[moveStep], getPosition().getX(), getPosition().getY(), null);
		} else {
			canvas.drawBitmap(spriteDie[dieStep], getPosition().getX(), getPosition().getY(), null);
		}

	}

	@Override
	public void play(State state) {

	}

	@Override
	public boolean isDestroyed() {
		return isDestroyed;
	}

	public enum Actions {
		MOVE, DIE
	}
}
