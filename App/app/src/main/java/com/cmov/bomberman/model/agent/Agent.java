package com.cmov.bomberman.model.agent;

import android.util.JsonWriter;
import com.cmov.bomberman.model.Event;
import com.cmov.bomberman.model.Position;
import com.cmov.bomberman.model.State;

import java.io.Serializable;

public abstract class Agent implements Serializable {
	public static final float WIDTH = 1f;
	public static final float HEIGHT = 1f;

	private Algorithm ai;
	private Position position;
	private String lastAction;
	private String currentAction;
	private int step;
	private int lastStep;
	private int id;

	public Agent(Position startingPos, Algorithm ai, int id) {
		position = startingPos;
		this.ai = ai;
		this.id = id;
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
	 *
	 * @param p the new position
	 */
	public void setPosition(Position p) {
		this.position = p;
	}

	/**
	 * @return the class name of the agent
	 */
	public String getType() {
		return this.getClass().getSimpleName();
	}

	/**
	 * @return the algorithm used by the agent
	 */
	protected Algorithm getAlgorithm() {
		return ai;
	}

	public void setAlgorithm(Algorithm ai) {
		this.ai = ai;
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
	 *
	 * @param state the game state
	 * @param dt    the time since the last update
	 */
	abstract public void play(final State state, final float dt);

	/**
	 * Returns if this agent is already destroyed (should be removed from the game)
	 *
	 * @return true if the agent is already destroyed, false otherwise.
	 */
	abstract public boolean isDestroyed();

	/**
	 * The default implementation is to pass the events to the algorithm.
	 *
	 * @param e the event
	 */
	public void handleEvent(Event e) {
		ai.handleEvent(e);
	}

	/**
	 * Prints this object in the json format.
	 *
	 * @param writer the json writer
	 */
	abstract public void toJson(JsonWriter writer);

	public int getLastStep() {
		return lastStep;
	}

	public void setLastStep(int lastStep) {
		this.lastStep = lastStep;
	}

	public int getStep() {

		return step;
	}

	public void setStep(int step) {
		this.step = step;
	}

	public Position getLeftCorner() {
		return new Position(position.getX() - WIDTH / 2, getPosition().getY());
	}

	public Position getRightCorner() {
		return new Position(position.getX() + WIDTH / 2, getPosition().getY());
	}

	public Position getTopCorner() {
		return new Position(getPosition().getX(), position.getY() - HEIGHT / 2);
	}

	public Position getBottomCorner() {
		return new Position(getPosition().getX(), position.getY() + HEIGHT / 2);
	}

	/**
	 * The possible actions for the most abstract agent.
	 */
	public enum Actions {
		DESTROY
	}
}
