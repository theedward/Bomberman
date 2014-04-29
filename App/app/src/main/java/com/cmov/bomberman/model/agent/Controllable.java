package com.cmov.bomberman.model.agent;

import com.cmov.bomberman.model.Event;

import java.util.Map;
import java.util.TreeMap;

public class Controllable implements Algorithm {
	private final Map<Character, String> keymap;
	private char lastKeyPressed;
	private boolean destroyMode;

	/**
	 * This constructor uses the default keymap.
	 */
	public Controllable() {
		this.keymap = new TreeMap<Character, String>() {{
			put('U', "MOVE_UP");
			put('L', "MOVE_LEFT");
			put('D', "MOVE_DOWN");
			put('R', "MOVE_RIGHT");
			put('B', "PUT_BOMB");
            put(' ', "");
        }};
	}

	/**
	 * A constructor with a custom keymap.
	 *
	 * @param keymap the keymap
	 */
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

    public char lastKeyPressed() {
        return lastKeyPressed;
    }

    /**
     * @return the action name mapped from the last key pressed or an empty string
	 * if no valid key was pressed.
	 */
	@Override
	public String getNextActionName() {

		if (destroyMode) {
			return Agent.Actions.DESTROY.toString();
		} else {
			return keymap.containsKey(lastKeyPressed) ? keymap.get(lastKeyPressed) : "";
		}
	}

	/**
	 * The agents that are controllable can override the Agent#handleEvent method to pass the events
	 * to the algorithm.
	 *
	 * @param e the event
	 */
	@Override
	public void handleEvent(final Event e) {
		if (e == Event.DESTROY) {
			destroyMode = true;
		}
	}
}
