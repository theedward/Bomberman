package com.cmov.bomberman.model;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

// This is where all the game will be processed.
public final class Game {
	private int level;
	private int time;
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
	 *
	 * @param username the player username
	 * @param p        the player object
	 *
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
	 *
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
	 * Creates all the objects and populates the game state.
	 */
	private void populateGame() {
		// Attribute each player a Bomberman object
		Player[] characterOwners = (Player[]) players.values().toArray();
		int playerCounter = 0;

		for (int i = 0; i < gameState.map.length; i++) {
			for (int j = 0; j < gameState.map[i].length; j++) {
				char character = gameState.map[i][j];
				// the position will be right in the middle
				final Position pos = new Position(i + 0.5f, j + 0.5f);
				if (character == State.Character.OBSTACLE.toChar()) {
					gameState.addCharacter(new Obstacle(gameState.createNewId(), pos));
				} else if (character == State.Character.BOMBERMAN.toChar()) {
					Bomberman bm = new Bomberman(gameState.createNewId(), pos,
												 characterOwners[playerCounter].getController(),
												 gameConfiguration.getExplosionRange(),
												 gameConfiguration.getbSpeed());
					gameState.addCharacter(bm);
					characterOwners[playerCounter].addAgent(bm);
				} else if (character == State.Character.ROBOT.toChar()) {
					gameState.addCharacter(new Robot(gameState.createNewId(), pos, gameConfiguration.getrSpeed()));
				}
			}
		}
	}

	/**
	 * Creates the game configuration and the game state.
	 * Calls Player#onGameStart for every registered player.
	 * Starts the game loop on a separate thread.
	 */
	public void start() {
		gameState.map = GameUtils.readLevelFromFile(level);
		gameConfiguration = GameUtils.readConfigurationFile(level);

		populateGame();

		// tell players that the game has started
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
	 *
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
	 *
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
	 * Verifies if the game has already finished.
	 */
	private boolean hasFinished() {
		// return if time has already finished
		if (time == 0) {
			return true;
		}

		// check how many are still alive
		int numMovableAgents = 0;
		final char[][] map = gameState.map;
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[i].length; j++) {
				if (map[i][j] == State.Character.BOMBERMAN.toChar() || map[i][j] == State.Character.ROBOT.toChar()) {
					numMovableAgents++;
				}
			}
		}

		// the game only finishes when only ONE movable agent is alive
		return numMovableAgents == 1;
	}

	/**
	 * The game loop.
	 * Performs the number of updates specified in the game configuration.
	 */
	private void loop() {
		final int sleepTime = 1000 / gameConfiguration.getNumUpdatesPerSecond();
		int timeCounter = 0;
		while (!hasFinished()) {
			update();
			timeCounter = (timeCounter + 1) % gameConfiguration.getNumUpdatesPerSecond();
			if (timeCounter == 0) {
				time--;
			}

			try {
				Thread.sleep(sleepTime);
			}
			catch (InterruptedException e) {}
		}
	}

	/**
	 * Updates the state (new frame). Updates every player with all the characters positions
	 */
	private void update() {
		// Update the state
		gameState.playAll();

		// Get all the character positions
		final Map<Integer, Position> positions = new TreeMap<Integer, Position>();
		for (Player p : players.values()) {
			for (Agent agent : p.getObjects()) {
				positions.put(agent.getId(), agent.getPosition());
			}
		}

		// Update every player with the character positions
		for (Player p : players.values()) {
			p.onUpdate(positions);
		}
	}
}
