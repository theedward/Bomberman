package com.cmov.bomberman.model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

// This is where all the game will be processed.
public final class Game {
	private int level;
	private Map<String, Player> players;
	private Map<String, Player> playersOnPause;
	private State gameState;
	private GameConfiguration gameConfiguration;

	public Game() {
		players = new HashMap<String, Player>();
		playersOnPause = new HashMap<String, Player>();
		gameState = new State();
	}

	/**
	 * Adds a new player to the game.
	 * @param username the player username
	 * @param p the player object
	 * @return true if the username is unique, false otherwise.
	 */
	public boolean addPlayer(String username, Player p) {
		if (players.containsKey(username)) {
			return false;
		} else {
			players.put(username, p);
			return true;
		}
	}

	/**
	 * Removes the player from the game.
	 * @param p the player object
	 */
	public void removePlayer(Player p) {
		players.remove(p);
		gameState.removeAll(p.getObjects());
	}

	private void pausePlayer(String username) {
		Player p = players.get(username);
		playersOnPause.put(username, p);
		removePlayer(p);
	}

	private void unpausePlayer(String username) {
		Player p = playersOnPause.get(username);
		playersOnPause.remove(p);
		addPlayer(username, p);
	}

	public void setLevel(final int level) {
		this.level = level;
	}

	/**
	 * Creates the game configuration and the game state.
	 * Calls Player#onGameStart for every registered player.
	 */
	public void start() {
		gameConfiguration = GameUtils.readConfigurationFile(level);
		// TODO create the game state
		for (Player p : players.values()) {
			p.onGameStart(gameConfiguration);
		}
	}

	public void end() {
		for (Player p : players.values()) {
			p.onGameEnd(gameConfiguration);
		}
	}

	/**
	 * Updates the state (new frame). Updates every player with all the characters positions
	 */
	public void update() {
		// Update the state
		gameState.playAll();

		List<Position> characterPositions = new LinkedList<Position>();
		for (Player p : players.values()) {
			for (Agent a : p.getObjects()) {
				characterPositions.add(a.getCurrentPos());
			}
		}
	}

	/**
	 * Pauses the game for the player with the given username
	 * @param username the player's username
	 */
	public void pause(String username) {
		pausePlayer(username);
		for (Player p : players.values()) {
			p.onGameUpdate(gameConfiguration);
		}
	}

	/**
	 * Unpauses the game for the player with the given username
	 * @param username the player's username
	 */
	public void unpause(String username) {
		unpausePlayer(username);
		for (Player p : players.values()) {
			p.onGameUpdate(gameConfiguration);
		}
	}

	/**
	 * Stops the game. All the game state is deleted.
	 */
	public void stop() {
		// TODO
	}

	/**
	 * Restarts the game. It's the same as Game#stop and Game#start.
	 */
	public void restart() {
		stop();
		start();
	}
}
