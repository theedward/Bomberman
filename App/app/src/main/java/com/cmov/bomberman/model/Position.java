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
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		} else {
			if (o instanceof Position) {
				Position pos = (Position) o;
				return (pos.getX() == this.getX()) && (pos.getY() == this.getY());
			}
			return false;
		}
	}

	public String toString() {
		return "X: " + x + " Y: " + y;
	}
}
