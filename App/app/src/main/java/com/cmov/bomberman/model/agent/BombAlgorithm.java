package com.cmov.bomberman.model.agent;

import com.cmov.bomberman.model.Event;

public class BombAlgorithm implements Algorithm {
	private int explosionTimeout;
	private long initialTime;

	public BombAlgorithm(int timeout) {
		initialTime = System.currentTimeMillis();
		explosionTimeout = timeout;
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
