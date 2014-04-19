package com.cmov.bomberman.model;

abstract class MovableAgent extends Agent {
	private static final float ROTATE_MARGIN = 0.25f;
	int speed;
	Axis lastAxis;

	;

	public MovableAgent(Position pos, Algorithm ai, int sp) {
		super(pos, ai);
		speed = sp;
		lastAxis = null;
	}

	;

	// Returns type of potential collision or null if there s none
	public Collision move(State currentState, Move direction) {
		/*
			if it's the first move or it's in the same axis as the previous move (horizontal or vertical),
			just move the agent.
			if the agent wants to change direction, he needs to be near the middle (0 + margin < pos < 1 - margin)
			in both axis, otherwise he won't move.
		 */
		final Axis moveAxis = (direction == Move.UP || direction == Move.DOWN) ? Axis.VERTICAL : Axis.HORIZONTAL;
		float x = getCurrentPos().getX();
		float y = getCurrentPos().getY();
		boolean canMove = false;
		if (lastAxis == null || lastAxis == moveAxis) {
			canMove = true;
		} else {
			final Position topLeft = new Position((float) Math.floor(x) + ROTATE_MARGIN,
												  (float) Math.floor(y) + ROTATE_MARGIN);
			final Position bottomRight = new Position((float) Math.ceil(x) - ROTATE_MARGIN,
													  (float) Math.ceil(y) - ROTATE_MARGIN);

			// Now the agent must be between these positions to change direction
			if (topLeft.getX() < x && x < bottomRight.getX() && topLeft.getY() < y && y < bottomRight.getY()) {
				canMove = true;
			}
		}

		// move if it's possible and update the last axis
		if (canMove) {
			lastAxis = moveAxis;
			if (moveAxis == Axis.HORIZONTAL) {
				x += (direction == Move.LEFT) ? -speed : speed;
			} else {
				y += (direction == Move.UP) ? -speed : speed;
			}
		}

		// handle collisions
		// map coordinates
		final Position curPos = new Position(x, y);
		char character = currentState.map[curPos.xToDiscrete()][curPos.yToDiscrete()];
		if (character == State.Character.EMPTY.toChar()) {
			setCurrentPos(new Position(x, y));
			return null;
		} else if (character == State.Character.BOMB.toChar() || character == State.Character.MOVABLE_AGENT.toChar()) {
			if (moveAxis == Axis.HORIZONTAL) {
				x = (direction == Move.LEFT) ? (float) Math.floor(x) + 0.5f : (float) Math.ceil(x) - 0.5f;
			} else {
				y = (direction == Move.UP) ? (float) Math.floor(y) + 0.5f : (float) Math.ceil(y) - 0.5f;
			}
			setCurrentPos(new Position(x, y));
			return Collision.WCHARACTER;
		} else if (character == State.Character.OBSTACLE.toChar() || character == State.Character.WALL.toChar()) {
			// correct position
			// set position in the opposite direction
			if (moveAxis == Axis.HORIZONTAL) {
				x = (direction == Move.LEFT) ? (float) Math.floor(x) + 0.5f : (float) Math.ceil(x) - 0.5f;
			} else {
				y = (direction == Move.UP) ? (float) Math.floor(y) + 0.5f : (float) Math.ceil(y) - 0.5f;
			}
			setCurrentPos(new Position(x, y));
			return Collision.WOBSTACLE;
		}

		// Should never happen
		System.out.println("MovableAgent#move: Unhandled case.");
		return null;
	}
	private enum Axis {
		HORIZONTAL, VERTICAL
	}

	public enum Move {
		UP, RIGHT, DOWN, LEFT
	}

	// possible collisions
	// with obstacle or with character
	public enum Collision {
		WOBSTACLE, WCHARACTER
	}
}
