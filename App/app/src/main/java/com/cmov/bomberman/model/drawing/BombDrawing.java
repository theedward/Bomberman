package com.cmov.bomberman.model.drawing;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.cmov.bomberman.model.GameUtils;
import com.cmov.bomberman.model.Position;
import com.cmov.bomberman.model.agent.Bomb;

public class BombDrawing extends Drawing {
    private static Bitmap sprite[];
    private static Bitmap explosionSprite[][];

    private final int range;

    public BombDrawing(final Position position, final int step, final int range, final String currentAction) {
        super(position);
        this.setStep(step);
        this.range = range;
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
            //				TODO: verify that when drawing the position is valid.. This only matters in this drawing
            //				because it's the only one that occupies more than a position

            // Left
            canvas.drawBitmap(explosionSprite[drawStep][0], x - range * spriteWidth, y, null);
            // Up
            canvas.drawBitmap(explosionSprite[drawStep][1], x, y - range * spriteHeight, null);
            // Right
            canvas.drawBitmap(explosionSprite[drawStep][2], x + range * spriteWidth, y, null);
            // Down
            canvas.drawBitmap(explosionSprite[drawStep][3], x, y + range * spriteHeight, null);
            // Center
            canvas.drawBitmap(explosionSprite[drawStep][4], x, y, null);

            // Vertical
            for (int hy = y - (range - 1) * spriteHeight; hy < y; hy += spriteWidth) {
                canvas.drawBitmap(explosionSprite[drawStep][5], x, hy, null);
            }
            for (int hy = y + (range - 1) * spriteHeight; hy > y; hy -= spriteWidth) {
                canvas.drawBitmap(explosionSprite[drawStep][5], x, hy, null);
            }

            // Horizontal
            for (int hx = x - (range - 1) * spriteWidth; hx < x; hx += spriteWidth) {
                canvas.drawBitmap(explosionSprite[drawStep][6], hx, y, null);
            }
            for (int hx = x + (range - 1) * spriteWidth; hx > x; hx -= spriteWidth) {
                canvas.drawBitmap(explosionSprite[drawStep][6], hx, y, null);
            }
        } else {
            canvas.drawBitmap(sprite[drawStep], x, y, null);
        }
    }
}
