package com.cmov.bomberman.model.drawing;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import com.cmov.bomberman.model.Position;

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

	}

	@Override
	public void draw(final Canvas canvas) {

	}
}
