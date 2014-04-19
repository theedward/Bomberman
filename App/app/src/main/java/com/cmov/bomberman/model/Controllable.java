package com.cmov.bomberman.model;

import java.util.Map;

/**
 * Created by JoãoEduardo on 15-04-2014.
 */
public class Controllable implements Algorithm {

	final Map<Character, String> keymap;
	char lastKeyPressed;
    private boolean destroyMode;

	public Controllable(Map<Character, String> keymap) {
		this.keymap = keymap;
	}

	/**
	 * Changes the last key pressed when the character is in the keymap.
	 *
	 * @return if the character is in the keymap.
	 */
	public boolean keyPressed(char c) {
		if (keymap.containsKey(c)) {
			lastKeyPressed = c;
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @return the action name mapped from the last key pressed or an empty string
	 * if no valid key was pressed.
	 */
	@Override
	public String getNextActionName() {

        if (destroyMode) {
            return AgentActions.DESTROY.toString();
        }else return keymap.containsKey(lastKeyPressed) ? keymap.get(lastKeyPressed) : "";
    }

	/**
	 * The agents that have are controllable, they override the Agent#handleEvent method.
	 *
	 * @param e
	 */
	@Override
	public void handleEvent(final Event e) {
        if (e == Event.DESTROY) {
            destroyMode = true;
        }
	}
}
