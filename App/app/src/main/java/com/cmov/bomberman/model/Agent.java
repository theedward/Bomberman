package com.cmov.bomberman.model;

public abstract class Agent implements Drawable, Playable {
	private final Algorithm ai;
	private Position currentPos;

	public Agent(Position startingPos, Algorithm ai) {
		currentPos = startingPos;
		this.ai = ai;
	}

	protected Position getCurrentPos() {
		return currentPos;
	}

	protected void setCurrentPos(Position p) {
		this.currentPos = p;
	}

	protected Algorithm getAlgorithm() {
		return ai;
	}
}
