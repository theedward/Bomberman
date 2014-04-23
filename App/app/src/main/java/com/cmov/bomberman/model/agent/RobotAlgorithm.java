package com.cmov.bomberman.model.agent;

import com.cmov.bomberman.model.Event;

public class RobotAlgorithm implements Algorithm {
	private boolean destroyMode;

	public RobotAlgorithm() {
		this.destroyMode = false;
	}

	@Override
	public String getNextActionName() {
		if (!destroyMode) {
			int nextMove = (int) (Math.random() * 4);
			switch (nextMove) {
				case 0:
					return MovableAgentActions.MOVE_BOTTOM.toString();
				case 1:
					return MovableAgentActions.MOVE_TOP.toString();
				case 2:
					return MovableAgentActions.MOVE_LEFT.toString();
				default:
					return MovableAgentActions.MOVE_RIGHT.toString();
			}
		} else {
			return AgentActions.DESTROY.toString();
		}
	}

	@Override
	public void handleEvent(Event e) {
		if (e == Event.DESTROY) {
			destroyMode = true;
		}
	}
}
