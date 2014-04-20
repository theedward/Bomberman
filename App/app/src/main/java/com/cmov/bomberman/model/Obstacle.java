package com.cmov.bomberman.model;

import android.util.JsonWriter;

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

	public Obstacle(final Position startingPos, String type) {
		super(startingPos, new Algorithm() {
			private boolean destroyMode;

			@Override
			public String getNextActionName() {
				if (destroyMode) {
					return AgentActions.DESTROY.toString();
				} else {
					return "";
				}
			}

			@Override
			public void handleEvent(final Event e) {
				if (e == Event.DESTROY) {
					destroyMode = true;
				}
			}
		},type);
	}

	@Override
	public void play(final State state) {
		if (step > 0 && step < MAX_STEP) {
			// increase step until it reaches MAX_STEP
			step++;
		} else if (step == MAX_STEP) {
			// when the MAX_STEP is reached, destroy the obstacle
			destroyed = true;
			return;
		} else {
			// check if the next action is DESTROY
			Algorithm ai = getAlgorithm();
			if (ai.getNextActionName().equals(AgentActions.DESTROY.toString())) {
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

            writer.name("currentAction").value("");
			writer.name("step").value(step);
			writer.endObject();
		}
		catch (IOException e) {

		}

	}
}
