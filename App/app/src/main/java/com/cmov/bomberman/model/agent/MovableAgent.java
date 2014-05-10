package com.cmov.bomberman.model.agent;

import android.util.Log;
import com.cmov.bomberman.model.Position;
import com.cmov.bomberman.model.State;

public abstract class MovableAgent extends Agent {
	private static final float ROTATE_MARGIN = 0.25f;
	private final String TAG = this.getClass().getSimpleName();

	private final float speed;
	private Axis lastAxis;

	public MovableAgent(final Position pos, final Algorithm ai, int id, final float speed) {
		super(pos, ai, id);
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
	 * @param action       the movement direction
	 * @param dt           the time passed since the last update
	 */
	public void move(State currentState, Actions action, float dt) {

		final Axis moveAxis =
				(action == Actions.MOVE_UP || action == Actions.MOVE_DOWN) ? Axis.VERTICAL : Axis.HORIZONTAL;
		final Position oldPosition = getPosition();
		float curX = oldPosition.getX();
		float curY = oldPosition.getY();
		boolean canMove = false;

		// if it's the first move or it's in the same axis as the previous move (horizontal or vertical),
		// the agent can move
		if (this.lastAxis == null || this.lastAxis == moveAxis) {
			canMove = true;
		} else {
			final Position topLeft = new Position((float) Math.floor(curX) + ROTATE_MARGIN,
												  (float) Math.floor(curY) + ROTATE_MARGIN);
			final Position bottomRight = new Position((float) Math.ceil(curX) - ROTATE_MARGIN,
													  (float) Math.ceil(curY) - ROTATE_MARGIN);

			// if the agent wants to change direction, he needs to be near the middle (0 + margin < pos < 1 - margin)
			// in both axis, otherwise he won't move.
			if (topLeft.getX() < curX && curX < bottomRight.getX() && topLeft.getY() < curY &&
				curY < bottomRight.getY()) {
				canMove = true;
				if (this.lastAxis == Axis.HORIZONTAL) {
					// Center the x position
					curX = (float) Math.floor(curX) + 0.5f;
				} else if (this.lastAxis == Axis.VERTICAL) {
					curY = (float) Math.floor(curY) + 0.5f;
				}
			}
		}

		if (canMove) {
			float targetX = curX;
			float targetY = curY;
			float nMoves;

			// calculate the new position and move to there
			this.lastAxis = moveAxis;
			if (moveAxis == Axis.HORIZONTAL) {
				targetX += ((action == Actions.MOVE_LEFT) ? -speed : speed) * dt;
				nMoves = Math.abs(targetX - curX);
			} else {
				targetY += ((action == Actions.MOVE_UP) ? -speed : speed) * dt;
				nMoves = Math.abs(targetY - curY);
			}

			for (; nMoves > 0; nMoves -= 1) {
				final float dist = nMoves > 1 ? 1 : nMoves;
				// handle collisions
				final char[][] map = currentState.getMap();
				final int mapX = Position.toDiscrete(curX);
				final int mapY = Position.toDiscrete(curY);

				if (action == Actions.MOVE_LEFT) {
					// left corner
					curX -= dist;
					final float leftX = curX - Agent.WIDTH / 2;
					final int mapLeftX = Position.toDiscrete(leftX);

					char c = map[mapY][mapLeftX];
					if (c == State.DrawingType.WALL.toChar() || c == State.DrawingType.OBSTACLE.toChar() ||
						c == State.DrawingType.BOMB.toChar()) {
						if (getType().equals("Bomberman")) {
							Log.e(TAG, "Collided with a " + c);
						}
						// move right
						curX = mapLeftX + 1 + Agent.WIDTH / 2;
					}
				} else if (action == Actions.MOVE_UP) {
					// top corner
					curY -= dist;
					final float topY = curY - Agent.HEIGHT / 2;
					final int mapTopY = Position.toDiscrete(topY);

					char c = map[mapTopY][mapX];
					if (c == State.DrawingType.WALL.toChar() || c == State.DrawingType.OBSTACLE.toChar() ||
						c == State.DrawingType.BOMB.toChar()) {
						if (getType().equals("Bomberman")) {
							Log.e(TAG, "Collided with a " + c);
						}
						// move bottom
						curY = mapTopY + 1 + Agent.HEIGHT / 2;
					}
				} else if (action == Actions.MOVE_RIGHT) {
					// right corner
					curX += dist;
					final float rightX = curX + Agent.WIDTH / 2;
					final int mapRightX = Position.toDiscrete(rightX);

					char c = map[mapY][mapRightX];
					if (c == State.DrawingType.WALL.toChar() || c == State.DrawingType.OBSTACLE.toChar() ||
						c == State.DrawingType.BOMB.toChar()) {
						if (getType().equals("Bomberman")) {
							Log.e(TAG, "Collided with a " + c);
						}
						// move left
						curX = mapRightX - Agent.WIDTH / 2;
					}

				} else if (action == Actions.MOVE_DOWN) {
					// bottom corner
					curY += dist;
					final float bottomY = curY + Agent.HEIGHT / 2;
					final int mapBottomY = Position.toDiscrete(bottomY);

					char c = map[mapBottomY][mapX];
					if (c == State.DrawingType.WALL.toChar() || c == State.DrawingType.OBSTACLE.toChar() ||
						c == State.DrawingType.BOMB.toChar()) {
						if (getType().equals("Bomberman")) {
							Log.e(TAG, "Collided with a " + c);
						}
						// move bottom
						curY = mapBottomY - Agent.HEIGHT / 2;
					}
				}

				setPosition(new Position(curX, curY));
				currentState.setMapEntry(oldPosition, State.DrawingType.EMPTY);
				currentState.setMapEntry(getPosition(), State.DrawingType.valueOf(getType().toUpperCase()));
			}
		}
	}

	private enum Axis {
		HORIZONTAL, VERTICAL
	}

	public enum Actions {
		MOVE_DOWN,
		MOVE_UP,
		MOVE_LEFT,
		MOVE_RIGHT;

		public static boolean isMovableAction(final String action) {
			return action.equals(MovableAgent.Actions.MOVE_LEFT.toString()) ||
				   action.equals(MovableAgent.Actions.MOVE_UP.toString()) ||
				   action.equals(MovableAgent.Actions.MOVE_RIGHT.toString()) ||
				   action.equals(MovableAgent.Actions.MOVE_DOWN.toString());
		}
	}
}
