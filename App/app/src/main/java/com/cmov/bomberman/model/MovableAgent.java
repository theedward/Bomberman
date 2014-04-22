package com.cmov.bomberman.model;

abstract class MovableAgent extends Agent {
	private static final float ROTATE_MARGIN = 0.25f;
	int speed;
	Axis lastAxis;


	public MovableAgent(final Position pos, final Algorithm ai, final int sp, String type) {
		super(pos, ai,type);
		speed = sp;
		lastAxis = null;
	}

    protected Move ActionToMove(String action) {

        if (action.equals(MovableAgentActions.MOVE_BOTTOM)) {
            return Move.DOWN;
        } else if (action.equals(MovableAgentActions.MOVE_TOP)) {
            return Move.UP;
        } else if (action.equals(MovableAgentActions.MOVE_LEFT)) {
            return Move.LEFT;
        } else if (action.equals(MovableAgentActions.MOVE_RIGHT)) {
            return Move.RIGHT;
        } else {
            return null;
        }
    }

	// Returns type of potential collision or null if there s none
	public boolean move(State currentState, Move direction) {
		/*
			if it's the first move or it's in the same axis as the previous move (horizontal or vertical),
			just move the agent.
			if the agent wants to change direction, he needs to be near the middle (0 + margin < pos < 1 - margin)
			in both axis, otherwise he won't move.
		 */
		final Axis moveAxis = (direction == Move.UP || direction == Move.DOWN) ? Axis.VERTICAL : Axis.HORIZONTAL;
		float x = getPosition().getX();
		float y = getPosition().getY();
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
			setPosition(new Position(x, y));
			return true;
		} else if (character == State.Character.BOMB.toChar() || character == State.Character.BOMBERMAN.toChar() ||
				   character == State.Character.ROBOT.toChar()) {
			if (moveAxis == Axis.HORIZONTAL) {
				x = (direction == Move.LEFT) ? (float) Math.floor(x) + 0.5f : (float) Math.ceil(x) - 0.5f;
			} else {
				y = (direction == Move.UP) ? (float) Math.floor(y) + 0.5f : (float) Math.ceil(y) - 0.5f;
			}
			setPosition(new Position(x, y));
            handleEvent(Event.DESTROY);
			return false;
		} else if (character == State.Character.OBSTACLE.toChar() || character == State.Character.WALL.toChar()) {
			// correct position
			// set position in the opposite direction
			if (moveAxis == Axis.HORIZONTAL) {
				x = (direction == Move.LEFT) ? (float) Math.floor(x) + 0.5f : (float) Math.ceil(x) - 0.5f;
			} else {
				y = (direction == Move.UP) ? (float) Math.floor(y) + 0.5f : (float) Math.ceil(y) - 0.5f;
			}
			setPosition(new Position(x, y));
			return false;
		}

		// Should never happen
		System.out.println("MovableAgent#move: Unhandled case.");
		return false;
	}

	private enum Axis {
		HORIZONTAL, VERTICAL
	}

	public enum Move {
		UP, RIGHT, DOWN, LEFT
	}
}
