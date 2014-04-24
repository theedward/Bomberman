package com.cmov.bomberman.model.drawing;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import com.cmov.bomberman.model.GameUtils;
import com.cmov.bomberman.model.Position;
import com.cmov.bomberman.model.agent.Agent;
import com.cmov.bomberman.model.agent.Robot;

public class RobotDrawing extends Drawing {
	private static Bitmap[][] spriteMove;
	private static Bitmap[] spriteDie;

	private int step;
	private String currentAction;

	public RobotDrawing(final Position position, final int step, final String currentAction) {
		super(position);
		this.step = step;
		this.currentAction = currentAction;

		if (spriteMove == null) {
			spriteMove = GameUtils.readRobotSprite();
		}

		if (spriteDie == null) {
			spriteDie = GameUtils.readRobotDestroyedSprite();
		}
	}

	@Override
	public void draw(Canvas canvas) {
		final int spriteWidth = spriteDie[0].getWidth();
		final int spriteHeight = spriteDie[0].getHeight();
		final int x = (int) getPosition().getX() * spriteWidth;
		final int y = (int) getPosition().getY() * spriteHeight;

		if (currentAction.equals(Agent.Actions.DESTROY.toString())) {
			canvas.drawBitmap(spriteDie[step], x, y, null);
		} else if (currentAction.equals(Robot.Actions.MOVE_LEFT.toString())) {
			canvas.drawBitmap(spriteMove[0][step], x, y, null);
		} else if (currentAction.equals(Robot.Actions.MOVE_RIGHT.toString())) {
			canvas.drawBitmap(spriteMove[1][step], x, y, null);
		}

		// TODO needs to choose the previous image when it's stopped or when moving up or down.
	}
}
