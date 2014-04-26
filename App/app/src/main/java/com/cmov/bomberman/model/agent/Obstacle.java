package com.cmov.bomberman.model.agent;

import android.util.JsonWriter;
import com.cmov.bomberman.model.Position;
import com.cmov.bomberman.model.State;

import java.io.IOException;

public class Obstacle extends Agent {
	private static final int MAX_STEP = 6;

	/**
	 * Step is the id of the image to be displayed.
	 */
	private int step;

	/**
	 * Represents if the obstacle is destroyed.
	 */
	private boolean destroyed;

	public Obstacle(final Position startingPos) {
		super(startingPos, new ObstacleAlgorithm());
		this.step = 0;
		this.destroyed = false;
	}

	@Override
	public void play(final State state, final long dt) {
		if (step > 0 && step < MAX_STEP) {
			// increase step until it reaches MAX_STEP
			step++;
		} else if (step == MAX_STEP) {
			// when the MAX_STEP is reached, destroy the obstacle
			destroyed = true;
		} else {
			// check if the next action is DESTROY
			Algorithm ai = getAlgorithm();
			if (ai.getNextActionName().equals(Agent.Actions.DESTROY.toString())) {
				step++;
			}
		}
	}

	@Override
	public boolean isDestroyed() {
		return destroyed;
	}

	@Override
	public void toJson(final JsonWriter writer) {
		try {
			writer.beginObject();
			writer.name("type").value(getType());

			writer.name("position");
			writer.beginArray();
			writer.value(getPosition().getX() - 0.5f);
			writer.value(getPosition().getY() - 0.5f);
			writer.endArray();

			writer.name("currentAction").value(getAlgorithm().getNextActionName());
            writer.name("lastAction").value("");
			writer.name("step").value(step);
			writer.endObject();
		}
		catch (IOException e) {
			System.out.println("Obstacle#toJson: Error while serializing to json.");
		}

	}
}
