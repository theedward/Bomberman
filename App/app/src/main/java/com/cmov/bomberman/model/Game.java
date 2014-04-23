package com.cmov.bomberman.model;

import android.util.JsonWriter;
import com.cmov.bomberman.model.agent.Agent;
import com.cmov.bomberman.model.agent.Bomberman;
import com.cmov.bomberman.model.agent.Obstacle;
import com.cmov.bomberman.model.agent.Robot;
import com.cmov.bomberman.model.drawing.Drawing;
import com.cmov.bomberman.model.drawing.WallDrawing;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * This is where all the game will be processed.
 */
public final class Game {
	private final Map<String, Player> players;
	private final Map<String, Player> playersOnPause;
	private final State gameState;
	private final GameConfiguration gameConfiguration;
	/**
	 * The number of updates the game will have.
	 */
	private int duration;
	private boolean hasStarted;

	public Game(final int level) {
		this.players = new HashMap<String, Player>();
		this.playersOnPause = new HashMap<String, Player>();
		this.gameState = new State();
		this.hasStarted = false;

		// Read data from files
		this.gameState.map = GameUtils.readLevelFromFile(level);
		this.gameConfiguration = GameUtils.readConfigurationFile(level);
		this.duration = gameConfiguration.getTimeLimit() * gameConfiguration.getNumUpdatesPerSecond();

		populateGame();
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
	 * @return the number of updates per second
	 */
	public int getNumberUpdates() {
		return this.gameConfiguration.getNumUpdatesPerSecond();
	}

	/**
	 * Removes the player from the game.
	 *
	 * @param p the player object
	 */
	public void removePlayer(Player p) {
		players.remove(p);
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

	/**
	 * Creates all the objects and populates the game state.
	 * Attributes each player a Bomberman.
	 */
	private void populateGame() {
		Player[] characterOwners = new Player[players.size()];
		players.values().toArray(characterOwners);

		List<Drawing> fixedDrawings = new LinkedList<Drawing>();
		int playerCounter = 0;

		for (int i = 0; i < gameState.map.length; i++) {
			for (int j = 0; j < gameState.map[i].length; j++) {
				char character = gameState.map[i][j];
				// the position will be right in the middle
				final Position pos = new Position(i + 0.5f, j + 0.5f);
				if (character == State.DrawingType.OBSTACLE.toChar()) {
					gameState.addAgent(new Obstacle(pos, "Obstacle"));
				} else if (character == State.DrawingType.BOMBERMAN.toChar()) {
					Bomberman bm = new Bomberman(pos, characterOwners[playerCounter].getController(),
												 gameConfiguration.getExplosionRange(), gameConfiguration.getbSpeed(),
												 "Bomberman", gameConfiguration.getExplosionDuration());
					bm.setOwnerUsername(characterOwners[playerCounter].getUsername());
					gameState.addAgent(bm);
				} else if (character == State.DrawingType.ROBOT.toChar()) {
					gameState.addAgent(new Robot(pos, gameConfiguration.getrSpeed(), "Robot"));
				} else if (character == State.DrawingType.WALL.toChar()) {
					fixedDrawings.add(new WallDrawing(new Position(i * GameUtils.IMG_WIDTH,
																   j * GameUtils.IMG_HEIGHT)));
				}
			}
		}

		gameConfiguration.setFixedDrawings(fixedDrawings);
	}

	/**
	 * Creates all the agents in the map.
	 * Calls Player#onGameStart for every registered player.
	 * Starts the game loop
	 */
	public void begin() {
		// tell players that the game has started
		for (Player p : players.values()) {
			p.onGameStart(gameConfiguration);
		}
	}

	public void end() {
		// tell players that the game has ended
		for (Player p : players.values()) {
			p.onGameEnd(gameConfiguration);
		}

		// TODO: compute scores, statistics, etc...
	}

	/**
	 * Updates the state (new frame).
	 */
	public void update() {
		if (!this.hasStarted) {
			this.begin();
		}

		// Update the state
		//		gameState.playAll();
		updatePlayers();
		this.duration--;

		if (this.hasFinished()) {
			this.end();
		}
	}

	/**
	 * Calls Player#onUpdate with the new state.
	 */
	private void updatePlayers() {
		// Get all the character positions
		StringWriter msg = new StringWriter();
		JsonWriter writer = new JsonWriter(msg);

		try {
			writer.setIndent("  ");
			writer.beginArray();
			for (Agent object : gameState.getObjects()) {
				object.toJson(writer);
			}
			writer.endArray();
			writer.close();
		}
		catch (IOException e) {
			System.out.println("Game#onUpdate: error creating json message.");
		}

		// Update every player with the character positions
		for (Player p : players.values()) {
			p.onUpdate(msg.toString());
		}
	}

	/**
	 * Verifies if the game has already finished.
	 */
	public boolean hasFinished() {
		// return if time has already finished
		if (duration == 0) {
			return true;
		}

		// check how many are still alive
		int numMovableAgents = 0;
		final char[][] map = gameState.map;
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[i].length; j++) {
				if (map[i][j] == State.DrawingType.BOMBERMAN.toChar() ||
					map[i][j] == State.DrawingType.ROBOT.toChar()) {
					numMovableAgents++;
				}
			}
		}

		// the game only finishes when only ONE movable agent is alive
		return numMovableAgents == 1;
	}

	/**
	 * Pauses the game for the player with the given username
	 *
	 * @param username the player's username
	 */
	public void pause(String username) {
		pausePlayer(username);
		gameState.pauseCharacter(username);
	}

	/**
	 * Unpauses the game for the player with the given username
	 *
	 * @param username the player's username
	 */
	public void unpause(String username) {
		unpausePlayer(username);
		gameState.unPauseCharacter(username);
	}
}
