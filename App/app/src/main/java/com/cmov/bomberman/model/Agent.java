package com.cmov.bomberman.model;

import android.util.JsonWriter;

public abstract class Agent {
	private final Algorithm ai;
	private Position position;
    private String type;

	public Agent(Position startingPos, Algorithm ai, String agentType) {
		position = startingPos;
		this.ai = ai;
        this.type = agentType;
	}

	protected Position getPosition() {
		return position;
	}

	protected void setPosition(Position p) {
		this.position = p;
	}

    protected String getType() { return type;}

	protected Algorithm getAlgorithm() {
		return ai;
	}

	abstract public void play(State state);

	abstract public boolean isDestroyed();

	/**
	 * The default implementation is to pass the events to the algorithm.
	 *
	 * @param e the event
	 */
	public void handleEvent(Event e) {
		ai.handleEvent(e);
	}

	abstract public void toJson(JsonWriter writer);
}
