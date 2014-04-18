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

	public float getY() {
		return y;
	}

	public void setX(final float x) {
		this.x = x;
	}

	public void setY(final float y) {
		this.y = y;
	}

    //this method converts float x coordinate into an int
    public int xToDiscrete() {
        return 0;
    }

    //this method converts float y coordinate into an int
    public int yToDiscrete() {
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if(o == null) {
            return false;
        }
        super.equals(o);
        Position pos = (Position) o;
        return (pos.getX() == this.getX()) && (pos.getY() == this.getY());
    }
}
