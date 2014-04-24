package com.cmov.bomberman.model.drawing;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import com.cmov.bomberman.model.GameUtils;
import com.cmov.bomberman.model.Position;
import com.cmov.bomberman.model.agent.Bomb;

public class BombDrawing extends Drawing {
	private static Bitmap sprite[];
	private static Bitmap explosionSprite[][];

	private final int step;
	private final int range;
	private final String currentAction;

	public BombDrawing(final Position position, final int step, final int range, final String currentAction) {
		super(position);
		this.step = step;
		this.range = range;
		this.currentAction = currentAction;

		if (sprite == null) {
			sprite = GameUtils.readBombSprite();
		}

		if (explosionSprite == null) {
			explosionSprite = GameUtils.readBombExplosionSprite();
		}
	}

	@Override
	public void draw(final Canvas canvas) {
		final int spriteWidth = sprite[0].getWidth();
		final int spriteHeight = sprite[0].getHeight();
		final int x = (int) getPosition().getX() * spriteWidth;
		final int y = (int) getPosition().getY() * spriteHeight;

		if (currentAction.equals(Bomb.Actions.EXPLODE.toString())) {
			/*
				TODO: verify that when drawing the position is valid.. This only matters in this drawing
				because it's the only one that occupies more than a position
			 */

			// Left
			canvas.drawBitmap(explosionSprite[step][0], y, x - range * spriteWidth, null);
			// Up
			canvas.drawBitmap(explosionSprite[step][1], y - range * spriteHeight, x, null);
			// Right
			canvas.drawBitmap(explosionSprite[step][2], y, x + range * spriteWidth, null);
			// Down
			canvas.drawBitmap(explosionSprite[step][3], y + range * spriteHeight, x, null);
			// Center
			canvas.drawBitmap(explosionSprite[step][4], y, x, null);

			// Vertical
			for (int hy = y - (range-1)*spriteHeight; hy < y; hy += spriteWidth) {
				canvas.drawBitmap(explosionSprite[step][5], hy, x, null);
			}
			for (int hy = y + (range-1)*spriteHeight; hy > y; hy -= spriteWidth) {
				canvas.drawBitmap(explosionSprite[step][5], hy, x, null);
			}

			// Horizontal
			for (int hx = x - (range-1)*spriteWidth; hx < x; hx += spriteWidth) {
				canvas.drawBitmap(explosionSprite[step][6], y, hx, null);
			}
			for (int hx = x + (range-1)*spriteWidth; hx > x; hx -= spriteWidth) {
				canvas.drawBitmap(explosionSprite[step][6], y, hx, null);
			}
		} else {
			canvas.drawBitmap(sprite[step], y, x, null);
		}
	}
}
