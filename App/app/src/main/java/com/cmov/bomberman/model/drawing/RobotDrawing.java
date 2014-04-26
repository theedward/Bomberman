package com.cmov.bomberman.model.drawing;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import com.cmov.bomberman.model.GameUtils;
import com.cmov.bomberman.model.Position;
import com.cmov.bomberman.model.agent.Agent;
import com.cmov.bomberman.model.agent.MovableAgent;

public class RobotDrawing extends Drawing {
	private static Bitmap[][] spriteMove;
	private static Bitmap[] spriteDie;

	public RobotDrawing(final Position position, final int step, final String currentAction) {
		super(position);
		this.setStep(step);
		this.setCurrentAction(currentAction);

		if (spriteMove == null) {
			spriteMove = GameUtils.readRobotSprite();
		}

		if (spriteDie == null) {
			spriteDie = GameUtils.readRobotDestroyedSprite();
		}
	}

    private int getIndexByAction(String action) {

        if (action.equals(MovableAgent.Actions.MOVE_LEFT.toString())) {
            return 0;
        } else if (action.equals(MovableAgent.Actions.MOVE_RIGHT.toString())) {
            return 1;
        }
        return 0;
    }

    @Override
	public void draw(Canvas canvas) {
		final int spriteWidth = spriteDie[0].getWidth();
		final int spriteHeight = spriteDie[0].getHeight();
		final int x = (int) getPosition().getX() * spriteWidth;
		final int y = (int) getPosition().getY() * spriteHeight;
        int spriteIndex = getIndexByAction(this.getCurrentAction());
        int drawStep = this.getStep();


        if (this.getCurrentAction().equals(MovableAgent.Actions.MOVE_DOWN.toString()) ||
                this.getCurrentAction().equals(MovableAgent.Actions.MOVE_UP.toString())) {
            spriteIndex = getIndexByAction(getLastAction());
            drawStep = this.getLastStep();
        }

        if (this.getCurrentAction().equals(Agent.Actions.DESTROY.toString())) {
            canvas.drawBitmap(spriteDie[drawStep], x, y, null);
        } else {
            canvas.drawBitmap(spriteMove[spriteIndex][drawStep], x, y, null);
        }
	}
}
