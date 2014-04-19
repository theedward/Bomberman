package com.cmov.bomberman.model;

public abstract class Agent implements Drawable {
	private final int id;
	private final Algorithm ai;
	private Position position;

	public Agent(int id, Position startingPos, Algorithm ai) {
		this.id = id;
		position = startingPos;
		this.ai = ai;
	}

	public int getId() {
		return id;
	}
	protected Position getPosition() {
		return position;
	}
	protected void setPosition(Position p) {
		this.position = p;
	}
	protected Algorithm getAlgorithm() {
		return ai;
	}
	abstract public void play(State state);
	abstract public boolean isDestroyed();
}
