package com.cmov.bomberman.model.agent;

import android.util.JsonWriter;
import com.cmov.bomberman.model.Event;
import com.cmov.bomberman.model.Position;
import com.cmov.bomberman.model.State;

public abstract class Agent {
	private final Algorithm ai;
	private Position position;
	private String type;

	public Agent(Position startingPos, Algorithm ai, String agentType) {
		position = startingPos;
		this.ai = ai;
		this.type = agentType;
	}

	public Position getPosition() {
		return position;
	}

	protected void setPosition(Position p) {
		this.position = p;
	}

	public String getType() { return type;}

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
