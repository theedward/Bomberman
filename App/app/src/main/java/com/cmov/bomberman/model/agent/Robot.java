package com.cmov.bomberman.model.agent;

import android.util.JsonWriter;
import com.cmov.bomberman.model.Position;
import com.cmov.bomberman.model.State;

import java.io.IOException;

public class Robot extends MovableAgent {
	private static final int MAX_DIE_STEP = 5;

	private boolean destroyed;

	public Robot(final Position position, int id, final int speed) {
		super(position, new RobotAlgorithm(), id, speed);
		this.destroyed = false;
		this.setStep(0);
	}

	@Override
	public void play(State state, final float dt) {
		String nextAction = getAlgorithm().getNextActionName();

		if (!nextAction.equals(Agent.Actions.DESTROY.toString())) {
			// The next action is moving
			MovableAgent.Actions action = MovableAgent.Actions.valueOf(nextAction);
			move(state, action, dt);
		}

		if (!this.getCurrentAction().equals(nextAction)) {
            this.setLastAction(this.getCurrentAction());
			this.setCurrentAction(nextAction);
			this.setLastStep(this.getStep());
            this.setStep(0);
		}

		if (this.getCurrentAction().equals(Agent.Actions.DESTROY.toString())) {
			if (this.getStep() < MAX_DIE_STEP) {
				this.setStep(this.getStep() + 1);
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
			writer.name("step").value(this.getStep());
            writer.name("lastStep").value(this.getLastStep());
            writer.name("id").value(this.getId());
            writer.name("isDestroyed").value(isDestroyed());
			writer.endObject();
		}
		catch (IOException e) {
			System.out.println("Robot#toJson: Error while serializing to json.");
		}
	}
}
