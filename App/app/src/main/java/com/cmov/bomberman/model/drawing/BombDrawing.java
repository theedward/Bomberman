package com.cmov.bomberman.model.drawing;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import com.cmov.bomberman.model.GameUtils;
import com.cmov.bomberman.model.Position;
import com.cmov.bomberman.model.agent.Bomb;

public class BombDrawing extends Drawing {
	private static Bitmap sprite[];
	private static Bitmap explosionSprite[][];

	private final int rangeRight;
    private final int rangeLeft;
    private final int rangeUp;
    private final int rangeDown;

	public BombDrawing(final Position position, final int step, final int rangeRight, final int rangeLeft, final int rangeUp, final int rangeDown, final String currentAction) {
		super(position);
		this.setStep(step);
		this.rangeRight = rangeRight;
        this.rangeLeft = rangeLeft;
        this.rangeUp = rangeUp;
        this.rangeDown = rangeDown;
		this.setCurrentAction(currentAction);

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
		final int x = (int) (getPosition().getX() * spriteWidth);
		final int y = (int) (getPosition().getY() * spriteHeight);
		int drawStep = this.getStep();

		if (this.getCurrentAction().equals(Bomb.Actions.EXPLODE.toString())) {

			// Left
            System.out.println("Drawing Bomb Down: " + rangeDown);
            System.out.println("Drawing Bomb Up: " + rangeUp);
            System.out.println("Drawing Bomb Left: " + rangeLeft);
            System.out.println("Drawing Bomb Right: " + rangeRight);
			canvas.drawBitmap(explosionSprite[drawStep][0], x - 1 * spriteWidth, y, null);
			// Up
			canvas.drawBitmap(explosionSprite[drawStep][1], x, y - 1 * spriteHeight, null);
			// Right
			canvas.drawBitmap(explosionSprite[drawStep][2], x + 1 * spriteWidth, y, null);
			// Down
			canvas.drawBitmap(explosionSprite[drawStep][3], x, y + 1 * spriteHeight, null);
			// Center
			canvas.drawBitmap(explosionSprite[drawStep][4], x, y, null);

			// Vertical
			for (int hy = y - (rangeDown) * spriteHeight; hy < y; hy += spriteWidth) {
				canvas.drawBitmap(explosionSprite[drawStep][5], x, hy, null);
			}
			for (int hy = y + (rangeUp) * spriteHeight; hy > y; hy -= spriteWidth) {
				canvas.drawBitmap(explosionSprite[drawStep][5], x, hy, null);
			}

			// Horizontal
			for (int hx = x - (rangeLeft) * spriteWidth; hx < x; hx += spriteWidth) {
				canvas.drawBitmap(explosionSprite[drawStep][6], hx, y, null);
			}
			for (int hx = x + (rangeRight) * spriteWidth; hx > x; hx -= spriteWidth) {
				canvas.drawBitmap(explosionSprite[drawStep][6], hx, y, null);
			}
		} else {
			canvas.drawBitmap(sprite[drawStep], x, y, null);
		}
	}
}
