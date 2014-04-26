package com.cmov.bomberman.model.agent;

import android.util.JsonWriter;
import com.cmov.bomberman.model.Event;
import com.cmov.bomberman.model.Position;
import com.cmov.bomberman.model.State;

public abstract class Agent {
	private final Algorithm ai;
	private Position position;
    private String lastAction;
    private String currentAction;
    private int id;

	public Agent(Position startingPos, Algorithm ai) {
		position = startingPos;
		this.ai = ai;
        this.lastAction = "";
        this.currentAction = "";
	}

	/**
	 * @return the current position of the agent (map coordinates)
	 */
	public Position getPosition() {
		return position;
	}

	/**
	 * Sets the current position of the agent (map coordinates)
	 * @param p the new position
	 */
	public void setPosition(Position p) {
		this.position = p;
	}

	/**
	 * @return the class name of the agent
	 */
	public String getType() { return this.getClass().getSimpleName(); }

	/**
	 * @return the algorithm used by the agent
	 */
	protected Algorithm getAlgorithm() {
		return ai;
	}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLastAction() {
        return lastAction;
    }

    public void setLastAction(String lastAction) {
        this.lastAction = lastAction;
    }

    public String getCurrentAction() {
        return currentAction;
    }

    public void setCurrentAction(String currentAction) {
        this.currentAction = currentAction;
    }

    /**
	 * Performs an action based on the algorithm. Must update the state with the new position.
	 * @param state the game state
	 * @param dt the time since the last update
	 */
	abstract public void play(final State state, final long dt);

	/**
	 * Returns if this agent is already destroyed (should be removed from the game)
	 * @return true if the agent is already destroyed, false otherwise.
	 */
	abstract public boolean isDestroyed();

	/**
	 * The default implementation is to pass the events to the algorithm.
	 * @param e the event
	 */
	public void handleEvent(Event e) {
		ai.handleEvent(e);
	}

	/**
	 * Prints this object in the json format.
	 * @param writer the json writer
	 */
	abstract public void toJson(JsonWriter writer);

	/**
	 * The possible actions for the most abstract agent.
	 */
	public enum Actions {
		DESTROY
	}
}
