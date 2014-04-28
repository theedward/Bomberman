package com.cmov.bomberman.model.drawing;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import com.cmov.bomberman.model.GameUtils;
import com.cmov.bomberman.model.Position;
import com.cmov.bomberman.model.agent.Agent;
import com.cmov.bomberman.model.agent.Bomberman;
import com.cmov.bomberman.model.agent.MovableAgent;

public class BombermanDrawing extends Drawing {
	private static Bitmap[][] sprite;

	public BombermanDrawing(final Position position, final int step, final String currentAction) {
		super(position);
		this.setStep(step);
		this.setCurrentAction(currentAction);

		if (sprite == null) {
			sprite = GameUtils.readBombermanSprite();
		}
	}

	private int getSpriteByAction(String action) {
		if (action.equals(MovableAgent.Actions.MOVE_DOWN.toString())) {
			return 0;
		} else if (action.equals(MovableAgent.Actions.MOVE_LEFT.toString())) {
			return 1;
		} else if (action.equals(MovableAgent.Actions.MOVE_UP.toString())) {
			return 2;
		} else if (action.equals(MovableAgent.Actions.MOVE_RIGHT.toString())) {
			return 3;
		} else if (action.equals(Agent.Actions.DESTROY.toString())) {
			return 5;
		}

		// Default sprite
		return 0;
	}

	@Override
	public void draw(Canvas canvas) {
		final int spriteWidth = sprite[0][0].getWidth();
		final int spriteHeight = sprite[0][0].getHeight();
		final int x = (int) (getPosition().getX() * spriteWidth);
		final int y = (int) (getPosition().getY() * spriteHeight);
		int spriteIndex = getSpriteByAction(this.getCurrentAction());
		int drawStep = this.getStep();

		if (this.getCurrentAction().equals(Bomberman.Actions.PUT_BOMB.toString())) {
			spriteIndex = getSpriteByAction(getLastAction());
			drawStep = this.getLastStep();
		}
		canvas.drawBitmap(sprite[spriteIndex][drawStep], x, y, null);
	}
}
