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
	private static Bitmap[][] otherSprite;

	private final boolean isMine;

	public BombermanDrawing(final boolean isMine, final Position position, final int step,
							final String currentAction) {
		super(position);
		this.setStep(step);
		this.setCurrentAction(currentAction);
		this.isMine = isMine;

		if (sprite == null) {
			sprite = GameUtils.getInstance().readBombermanSprite();
			otherSprite = GameUtils.getInstance().readOtherBombermanSprite();
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

		if (this.getCurrentAction().equals(Bomberman.Actions.PUT_BOMB.toString()) ||
			this.getCurrentAction().equals("")) {
			spriteIndex = getSpriteByAction(getLastAction());
			drawStep = this.getLastStep();
		}

		if (isMine) {
			canvas.drawBitmap(sprite[spriteIndex][drawStep], x, y, null);
		} else {
			canvas.drawBitmap(otherSprite[spriteIndex][drawStep], x, y, null);
		}
	}
}
