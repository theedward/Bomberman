package com.cmov.bomberman.model;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class BombDrawing extends Drawing {
	private static final int BOMB_SPRITE_LINE = 1;
	private static final int BOMB_SPRITE_COLUMN = 6;
	private static final int BOMB_NUM_IMAGES = 3;
	private static final int EXPL_SPRITE_LINE = 11;
	private static final int EXPL_SPRITE_COLUMN = 0;
	private static final int EXPL_NUM_IMAGES = 4;

	private static Bitmap bombSprite[];
	private static Bitmap explosionSprite[];

	private int bombStep;
	private int explStep;
	private int timeout;

	public BombDrawing(final Position position, final int bombStep, final int explStep, final int timeout) {
		super(position);
		this.bombStep = bombStep;
		this.explStep = explStep;
		this.timeout = timeout;

		if (bombSprite == null) {
			bombSprite = GameUtils.readCharacterSprite(BOMB_SPRITE_LINE, BOMB_SPRITE_COLUMN, BOMB_NUM_IMAGES);
		}

		if (explosionSprite == null) {
			explosionSprite = GameUtils.readCharacterSprite(EXPL_SPRITE_LINE, EXPL_SPRITE_COLUMN, EXPL_NUM_IMAGES);
		}
	}

	@Override
	public void draw(final Canvas canvas) {
		if (timeout != 0) {
			canvas.drawBitmap(bombSprite[bombStep], getPosition().getX(), getPosition().getY(), null);
		} else {
			canvas.drawBitmap(explosionSprite[explStep], getPosition().getX(), getPosition().getY(), null);
		}
	}
}
