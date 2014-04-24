package com.cmov.bomberman.model.drawing;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import com.cmov.bomberman.model.GameUtils;
import com.cmov.bomberman.model.Position;
import com.cmov.bomberman.model.agent.Agent;
import com.cmov.bomberman.model.agent.MovableAgent;

public class BombermanDrawing extends Drawing {
	private static Bitmap[][] sprite;

	private int step;
	private String currentAction;

	public BombermanDrawing(final Position position, final int step, final String currentAction) {
		super(position);
		this.step = step;
		this.currentAction = currentAction;

		if (sprite == null) {
			sprite = GameUtils.readBombermanSprite();
		}
	}


	@Override
	public void draw(Canvas canvas) {
		final int spriteWidth = sprite[0][0].getWidth();
		final int spriteHeight = sprite[0][0].getHeight();
		final int x = (int) getPosition().getX() * spriteWidth;
		final int y = (int) getPosition().getY() * spriteHeight;
		
		if (currentAction.equals(MovableAgent.Actions.MOVE_DOWN.toString())) {
			canvas.drawBitmap(sprite[0][step], y, x, null);
		} else if (currentAction.equals(MovableAgent.Actions.MOVE_LEFT.toString())) {
			canvas.drawBitmap(sprite[1][step], y, x, null);
		} else if (currentAction.equals(MovableAgent.Actions.MOVE_UP.toString())) {
			canvas.drawBitmap(sprite[2][step], y, x, null);
		} else if (currentAction.equals(MovableAgent.Actions.MOVE_RIGHT.toString())) {
			canvas.drawBitmap(sprite[3][step], y, x, null);
		} else if (currentAction.equals(Agent.Actions.DESTROY.toString())) {
			canvas.drawBitmap(sprite[5][step], y, x, null);
		}

		// TODO when the action is put bomb, must use the previous sprite.
	}
}
