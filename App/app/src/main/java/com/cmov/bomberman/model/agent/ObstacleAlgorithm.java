package com.cmov.bomberman.model.agent;

import com.cmov.bomberman.model.Event;

import java.io.Serializable;

public class ObstacleAlgorithm implements Algorithm, Serializable {
	private boolean destroyMode;

	@Override
	public String getNextActionName() {
		return destroyMode ? Agent.Actions.DESTROY.toString() : "";
	}

	@Override
	public void handleEvent(final Event e) {
		if (e == Event.DESTROY) {
			destroyMode = true;
		}
	}
}
