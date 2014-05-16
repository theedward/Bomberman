package com.cmov.bomberman.model.agent;

import com.cmov.bomberman.model.Event;

import java.io.Serializable;

public class NullAlgorithm implements Algorithm, Serializable {
	@Override
	public String getNextActionName() {
		return "";
	}

	@Override
	public void handleEvent(final Event e) {
		// Empty on purpose
	}
}
