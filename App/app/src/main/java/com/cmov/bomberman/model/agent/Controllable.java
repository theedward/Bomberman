package com.cmov.bomberman.model.agent;

import com.cmov.bomberman.model.Event;

import java.util.Map;
import java.util.TreeMap;

public class Controllable implements Algorithm {
	public static final Map<Character, String> DEFAULT_KEYMAP = new TreeMap<Character, String>() {{
		put('U', "MOVE_TOP");
		put('L', "MOVE_LEFT");
		put('D', "MOVE_BOTTOM");
		put('R', "MOVE_RIGHT");
		put('B', "PUT_BOMB");
	}};

	final Map<Character, String> keymap;
	char lastKeyPressed;
	private boolean destroyMode;

	public Controllable() {
		this.keymap = DEFAULT_KEYMAP;
	}

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
		} else {
			return keymap.containsKey(lastKeyPressed) ? keymap.get(lastKeyPressed) : "";
		}
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
