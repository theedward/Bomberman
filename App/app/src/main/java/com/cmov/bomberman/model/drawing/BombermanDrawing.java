package com.cmov.bomberman.model.drawing;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import com.cmov.bomberman.model.Position;

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
	}


	@Override
	public void draw(Canvas canvas) {
		// TODO
	}
}
