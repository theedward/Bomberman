package com.cmov.bomberman.model.agent;

import com.cmov.bomberman.model.Event;

/**
 * This class allows to create algorithms or even to control any object that
 * depends on this interface.
 */
public interface Algorithm {
	/**
	 * @return the next action name
	 */
	public String getNextActionName();

	/**
	 * This method provides support for the algorithm to change considering game events.
	 *
	 * @param e the event
	 */
	public void handleEvent(Event e);
}
