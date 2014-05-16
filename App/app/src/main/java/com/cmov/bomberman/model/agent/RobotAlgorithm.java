package com.cmov.bomberman.model.agent;

import com.cmov.bomberman.model.Event;

import java.io.Serializable;

public class RobotAlgorithm implements Algorithm, Serializable {
	private boolean destroyMode;
	private MovableAgent.Actions move;
	private boolean hasCollided;
	private boolean triedOppositeDirection;

	/**
	 * The first direction is random
	 */
	public RobotAlgorithm() {
		this.destroyMode = false;
		int dir = (int) (Math.random() % 4);
		this.move = MovableAgent.Actions.values()[dir];
		this.hasCollided = false;
		this.triedOppositeDirection = false;
	}

	@Override
	public String getNextActionName() {
		if (destroyMode) {
			return Agent.Actions.DESTROY.toString();
		} else {
			if (hasCollided && triedOppositeDirection) {
				// Tried both directions and failed, switch axis
				if (move == MovableAgent.Actions.MOVE_DOWN || move == MovableAgent.Actions.MOVE_UP) {
					move = ((int) (Math.random() % 2)) == 0 ? MovableAgent.Actions.MOVE_LEFT
															: MovableAgent.Actions.MOVE_RIGHT;
				} else {
					move = ((int) (Math.random() % 2)) == 0 ? MovableAgent.Actions.MOVE_UP
															: MovableAgent.Actions.MOVE_DOWN;
				}

				hasCollided = false;
				triedOppositeDirection = false;
			} else if (hasCollided) {
				if (move == MovableAgent.Actions.MOVE_DOWN) {
					move = MovableAgent.Actions.MOVE_UP;
				} else if (move == MovableAgent.Actions.MOVE_UP) {
					move = MovableAgent.Actions.MOVE_DOWN;
				} else if (move == MovableAgent.Actions.MOVE_LEFT) {
					move = MovableAgent.Actions.MOVE_RIGHT;
				} else if (move == MovableAgent.Actions.MOVE_RIGHT) {
					move = MovableAgent.Actions.MOVE_LEFT;
				}

				triedOppositeDirection = true;
				hasCollided = false;
			} else {
				hasCollided = false;
				triedOppositeDirection = false;
			}
		}

		return move.toString();
	}

	@Override
	public void handleEvent(Event e) {
		if (e == Event.DESTROY) {
			destroyMode = true;
		} else if (e == Event.COLLISION) {
			hasCollided = true;
		}
	}
}
