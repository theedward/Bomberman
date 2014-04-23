package com.cmov.bomberman.model.agent;

import android.util.JsonWriter;
import com.cmov.bomberman.model.Position;
import com.cmov.bomberman.model.State;

import java.io.IOException;

public class Robot extends MovableAgent {
	private static final int MAX_DIE_STEP = 5;

	private boolean isDestroyed;
	private String currentAction = "";
	private int step;

	public Robot(final Position position, final int speed, String type) {
		super(position, new RobotAlgorithm(), speed, type);
	}

	@Override
	public void play(State state) {
		String nextAction = getAlgorithm().getNextActionName();
		Move move = ActionToMove(nextAction);
		move(state, move);

		if (!currentAction.equals(nextAction)) {
			currentAction = nextAction;
			step = 0;
		} else if (currentAction.equals(AgentActions.DESTROY.toString())) {
			if (step < MAX_DIE_STEP) {
				step++;
			} else {
				isDestroyed = true;
			}
		}
	}

	@Override
	public boolean isDestroyed() {
		return isDestroyed;
	}

	@Override
	public void toJson(JsonWriter writer) {
		try {
			writer.beginObject();
			writer.name("type").value(getType());

			writer.name("position");
			writer.beginArray();
			writer.value(getPosition().getX() - 0.5f);
			writer.value(getPosition().getY() - 0.5f);
			writer.endArray();

			writer.name("currentAction").value(currentAction);
			writer.name("step").value(0);
			writer.endObject();
		}
		catch (IOException e) {

		}
	}
}
