package com.cmov.bomberman.model.drawing;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import com.cmov.bomberman.model.GameUtils;
import com.cmov.bomberman.model.Position;
import com.cmov.bomberman.model.agent.Bomb;

public class BombDrawing extends Drawing {
	private static Bitmap sprite[];
	private static Bitmap explosionSprite[][];

	private int rangeRight;
	private int rangeLeft;
	private int rangeUp;
	private int rangeDown;

	public BombDrawing(final Position position, final int rangeRight, final int rangeLeft, final int rangeUp,
					   final int rangeDown) {
		super(position);
		this.setStep(0);
		this.setCurrentAction("");

		this.rangeRight = rangeRight;
		this.rangeLeft = rangeLeft;
		this.rangeUp = rangeUp;
		this.rangeDown = rangeDown;

		if (sprite == null) {
			sprite = GameUtils.getInstance().readBombSprite();
		}

		if (explosionSprite == null) {
			explosionSprite = GameUtils.getInstance().readBombExplosionSprite();
		}
	}

	public void setRangeRight(int rangeRight) {
		this.rangeRight = rangeRight;
	}

	public void setRangeLeft(int rangeLeft) {
		this.rangeLeft = rangeLeft;
	}

	public void setRangeUp(int rangeUp) {
		this.rangeUp = rangeUp;
	}

	public void setRangeDown(int rangeDown) {
		this.rangeDown = rangeDown;
	}

	@Override
	public void draw(final Canvas canvas) {
		final int spriteWidth = sprite[0].getWidth();
		final int spriteHeight = sprite[0].getHeight();
		final int x = (int) (getPosition().getX() * spriteWidth);
		final int y = (int) (getPosition().getY() * spriteHeight);
		int drawStep = this.getStep();

		if (this.getCurrentAction().equals(Bomb.Actions.EXPLODE.toString())) {
			// Vertical
			for (int hy = y - (rangeUp - 1) * spriteHeight; hy < y; hy += spriteWidth) {
				canvas.drawBitmap(explosionSprite[drawStep][5], x, hy, null);
			}
			for (int hy = y + (rangeDown - 1) * spriteHeight; hy > y; hy -= spriteWidth) {
				canvas.drawBitmap(explosionSprite[drawStep][5], x, hy, null);
			}

			// Horizontal
			for (int hx = x - (rangeLeft - 1) * spriteWidth; hx < x; hx += spriteWidth) {
				canvas.drawBitmap(explosionSprite[drawStep][6], hx, y, null);
			}
			for (int hx = x + (rangeRight - 1) * spriteWidth; hx > x; hx -= spriteWidth) {
				canvas.drawBitmap(explosionSprite[drawStep][6], hx, y, null);
			}

			// Left
			if (rangeLeft != 0) {
				canvas.drawBitmap(explosionSprite[drawStep][0], x - rangeLeft * spriteWidth, y, null);
			}
			// Up
			if (rangeUp != 0) {
				canvas.drawBitmap(explosionSprite[drawStep][1], x, y - rangeUp * spriteHeight, null);
			}
			// Right
			if (rangeRight != 0) {
				canvas.drawBitmap(explosionSprite[drawStep][2], x + rangeRight * spriteWidth, y, null);
			}
			// Down
			if (rangeDown != 0) {
				canvas.drawBitmap(explosionSprite[drawStep][3], x, y + rangeDown * spriteHeight, null);
			}
			// Center
			canvas.drawBitmap(explosionSprite[drawStep][4], x, y, null);
		} else {
			canvas.drawBitmap(sprite[drawStep], x, y, null);
		}
	}
}
