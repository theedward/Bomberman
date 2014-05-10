package com.cmov.bomberman.model.agent;

import com.cmov.bomberman.model.Event;

public class BombAlgorithm implements Algorithm {
	private float explosionTimeout;
	private long initialTime;

	public BombAlgorithm(float timeout) {
		initialTime = System.currentTimeMillis();
		explosionTimeout = timeout * 1000f;
	}

	@Override
	public String getNextActionName() {
		long currentTime = System.currentTimeMillis();
		if (currentTime - initialTime < explosionTimeout) {
			return "";
		} else {
			return Bomb.Actions.EXPLODE.toString();
		}
	}

	@Override
	public void handleEvent(Event e) {
		// Nothing to do here
	}
}
