package com.cmov.bomberman.model.agent;

import com.cmov.bomberman.model.Position;
import com.cmov.bomberman.model.State;

abstract class MovableAgent extends Agent {
	private static final float ROTATE_MARGIN = 0.25f;

	private final int speed;
	private Axis lastAxis;

	public MovableAgent(final Position pos, final Algorithm ai, final int speed) {
		super(pos, ai);
		this.speed = speed;
		this.lastAxis = null;
	}

	/**
	 * The movement algorithm is divided in small steps.
	 * In the first step, it verifies if it's possible to move.
	 * In the second step, it calculates the new position.
	 * In the third step, it handles the collisions.
	 * At last, it updates the position and the state.
	 *
	 * @param currentState the current state
	 * @param action the movement direction
	 * @param dt the time passed since the last update
	 */
	public void move(State currentState, Actions action, long dt) {
		final Axis moveAxis = (action == Actions.MOVE_UP || action == Actions.MOVE_DOWN) ? Axis.VERTICAL : Axis.HORIZONTAL;
		final Position oldPosition = getPosition();
		float x = oldPosition.getX();
		float y = oldPosition.getY();
		boolean canMove = false;

		// if it's the first move or it's in the same axis as the previous move (horizontal or vertical),
		// the agent can move
		if (this.lastAxis == null || this.lastAxis == moveAxis) {
			canMove = true;
		} else {
			final Position topLeft = new Position((float) Math.floor(x) + ROTATE_MARGIN,
												  (float) Math.floor(y) + ROTATE_MARGIN);
			final Position bottomRight = new Position((float) Math.ceil(x) - ROTATE_MARGIN,
													  (float) Math.ceil(y) - ROTATE_MARGIN);

			// if the agent wants to change direction, he needs to be near the middle (0 + margin < pos < 1 - margin)
			// in both axis, otherwise he won't move.
			if (topLeft.getX() < x && x < bottomRight.getX() && topLeft.getY() < y && y < bottomRight.getY()) {
				canMove = true;
			}
		}

		if (canMove) {
			// calculate the new position
			this.lastAxis = moveAxis;
			if (moveAxis == Axis.HORIZONTAL) {
				x += ((action == Actions.MOVE_LEFT) ? -speed : speed) * dt;
			} else {
				y += ((action == Actions.MOVE_DOWN) ? -speed : speed) * dt;
			}

			// It's not needed to check if the position is valid because all the movable agents
			// are surrounded by other agents.

			// handle collisions
			Position newPosition = new Position(x, y);
			char character = currentState.getMap()[newPosition.xToDiscrete()][newPosition.yToDiscrete()];
			if (character != State.DrawingType.EMPTY.toChar()) {
				// the new position will take into account the direction of the agent.
				// ex: if the agent moved up and hit a wall, it must move down to the position
				// right before hitting the wall.
				if (moveAxis == Axis.HORIZONTAL) {
					x = (action == Actions.MOVE_LEFT) ? (float) Math.floor(x) + 0.5f : (float) Math.ceil(x) - 0.5f;
				} else {
					y = (action == Actions.MOVE_UP) ? (float) Math.floor(y) + 0.5f : (float) Math.ceil(y) - 0.5f;
				}
				newPosition = new Position(x, y);
			}

			currentState.setMapPosition(newPosition, oldPosition);
			setPosition(newPosition);
		}
	}

	private enum Axis {
		HORIZONTAL, VERTICAL
	}
	
	public enum Actions {
		MOVE_DOWN,
		MOVE_UP,
		MOVE_LEFT,
		MOVE_RIGHT,
	}
}
