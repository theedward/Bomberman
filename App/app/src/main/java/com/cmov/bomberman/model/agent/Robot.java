package com.cmov.bomberman.model.agent;

import android.util.JsonWriter;
import com.cmov.bomberman.model.Position;
import com.cmov.bomberman.model.State;

import java.io.IOException;

public class Robot extends MovableAgent {
	private static final int MAX_DIE_STEP = 5;

	private boolean destroyed;
	private int step;

	public Robot(final Position position, final int speed) {
		super(position, new RobotAlgorithm(), speed);
		this.destroyed = false;
		this.step = 0;
	}

	@Override
	public void play(State state, final long dt) {
		String nextAction = getAlgorithm().getNextActionName();
		MovableAgent.Actions action = MovableAgent.Actions.valueOf(nextAction);

		if (action != null) {
			// The next action is moving
			move(state, action, dt);
		}

		if (! this.getCurrentAction().equals(nextAction)) {
            this.setLastAction(this.getCurrentAction());
			this.setCurrentAction(nextAction);
			step = 0;
		}

		if (this.getCurrentAction().equals(Agent.Actions.DESTROY.toString())) {
			if (step < MAX_DIE_STEP) {
				step++;
			} else {
				destroyed = true;
			}
		}
	}

	@Override
	public boolean isDestroyed() {
		return destroyed;
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

			writer.name("currentAction").value(this.getCurrentAction());
            writer.name("lastAction").value(this.getLastAction());
			writer.name("step").value(0);
			writer.endObject();
		}
		catch (IOException e) {
			System.out.println("Robot#toJson: Error while serializing to json.");
		}
	}
}
