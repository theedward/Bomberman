package com.cmov.bomberman.model;

public class Position {
	private float x;
	private float y;

	public Position() {
	}

	public Position(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public float getX() {
		return x;
	}

	public void setX(final float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(final float y) {
		this.y = y;
	}

	/**
	 * Converts a float position into a discrete position.
	 * @return the closest discrete x
	 */
	public int xToDiscrete() {
		return (int) Math.round(x);
	}

	/**
	 * Converts a float position into a discrete position. The value .5 is rounded below
	 * @return the closest discrete x
	 */
	public int yToDiscrete() {
		return (int) Math.round(y);
	}

	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		super.equals(o);
		Position pos = (Position) o;
		return (pos.getX() == this.getX()) && (pos.getY() == this.getY());
	}

	public String toString() {
		return "X: " + x + " Y: " + y;
	}
}
