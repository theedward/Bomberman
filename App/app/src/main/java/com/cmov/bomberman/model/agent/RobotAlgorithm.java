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
					return MovableAgent.Actions.MOVE_DOWN.toString();
				case 1:
					return MovableAgent.Actions.MOVE_UP.toString();
				case 2:
					return MovableAgent.Actions.MOVE_LEFT.toString();
				default:
					return MovableAgent.Actions.MOVE_RIGHT.toString();
			}
		} else {
			return Agent.Actions.DESTROY.toString();
		}
	}

	@Override
	public void handleEvent(Event e) {
		if (e == Event.DESTROY) {
			destroyMode = true;
		}
	}
}
