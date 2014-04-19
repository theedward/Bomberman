package com.cmov.bomberman.model;

import java.util.*;

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
	 * Starts the game loop on a separate thread.
	 */
	public void start() {
		gameConfiguration = GameUtils.readConfigurationFile(level);
		// TODO create the game state
		for (Player p : players.values()) {
			p.onGameStart(gameConfiguration);
		}

		new Thread(new Runnable() {
			@Override
			public void run() {
				loop();
			}
		}).start();
	}

	public void end() {
		for (Player p : players.values()) {
			p.onGameEnd(gameConfiguration);
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

	/**
	 * TODO: when does the game finish?
	 * Verifies if the game has already finished.
	 * @return true when?
	 */
	private boolean hasFinished() {
		// TODO
		return false;
	}

	/**
	 * The game loop.
	 * Performs the number of updates specified in the game configuration.
	 */
	private void loop() {
		final int sleepTime = 1000 / gameConfiguration.getNumUpdatesPerSecond();
		while (! hasFinished()) {
			update();
			try {
				Thread.sleep(sleepTime);
			}
			catch (InterruptedException e) {
				// TODO: improve comment
				System.out.println("They don't let me sleep");
			}
		}
	}

	/**
	 * Updates the state (new frame). Updates every player with all the characters positions
	 */
	private void update() {
		// Update the state
		gameState.playAll();

		// Get all the character positions
		final Map<String, Position[]> characterPositions = new TreeMap<String, Position[]>();
		for (Player p : players.values()) {
			final Agent[] objects = (Agent[]) p.getObjects().toArray();
			final Position[] positions = new Position[objects.length];
			for (int i = 0; i < positions.length; i++) {
				positions[i] = objects[i].getCurrentPos();
			}
			characterPositions.put(p.getUsername(), positions);
		}

		// Update every player with the character positions
		for (Player p : players.values()) {
			p.onUpdate(characterPositions);
		}
	}
}
