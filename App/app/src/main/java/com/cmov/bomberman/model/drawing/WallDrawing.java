package com.cmov.bomberman.model.drawing;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import com.cmov.bomberman.model.GameUtils;
import com.cmov.bomberman.model.Position;

public class WallDrawing extends Drawing {
    private static Bitmap sprite;

    public WallDrawing(final Position position) {
        super(position);
        if (sprite == null) {
            sprite = GameUtils.getInstance().readWallSprite();
        }
    }

    @Override
    public void draw(final Canvas canvas) {
        final int spriteWidth = sprite.getWidth();
        final int spriteHeight = sprite.getHeight();
        final int x = (int) (getPosition().getX() * spriteWidth);
        final int y = (int) (getPosition().getY() * spriteHeight);
        canvas.drawBitmap(sprite, x, y, null);
    }
}
