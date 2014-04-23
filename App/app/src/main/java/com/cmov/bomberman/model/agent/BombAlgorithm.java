package com.cmov.bomberman.model.agent;

import com.cmov.bomberman.model.Event;

public class BombAlgorithm implements Algorithm {

	private boolean destroyMode;
	private int explosionTimeout;
	private long initialTime;

	public BombAlgorithm(long time, int timeout) {
		initialTime = time;
		explosionTimeout = timeout;
	}

	@Override
	public String getNextActionName() {
		long currentTime = System.currentTimeMillis();

		if ((currentTime - initialTime) >= explosionTimeout) {
			return "EXPLODE";
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
