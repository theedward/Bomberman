package com.cmov.bomberman.model;

public class Position {
	private final float x;
	private final float y;

	public Position(float x, float y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Converts a float position into a discrete position.
	 */
	public static int toDiscrete(float value) {
		return (int) Math.floor(value);
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public int xToDiscrete() {
		return (int) Math.floor(this.x);
	}

	public int yToDiscrete() {
		return (int) Math.floor(this.y);
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof Position) {
			Position o = (Position) other;
			return this.x == o.x && this.y == o.y;
		} else {
			return false;
		}
	}

	public boolean equalsInMap(Position other) {
		return (other.xToDiscrete() == this.xToDiscrete()) && (other.yToDiscrete() == this.yToDiscrete());
	}

	public String toString() {
		return "X: " + x + " Y: " + y;
	}
}
