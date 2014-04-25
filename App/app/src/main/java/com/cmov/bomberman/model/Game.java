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

	/**
	 * This constructor performs all the necessary steps to start the game right next.
	 * However, the game would be without any player.
	 *
	 * @param level the level to be played in this game
	 */
	public Game(final int level) {
		this.players = new HashMap<String, Player>();
		this.playersOnPause = new HashMap<String, Player>();
		this.gameState = new State();
		this.hasStarted = false;

		// Read data from files
		this.gameState.setMap(GameUtils.readLevelFromFile(level));
		this.gameConfiguration = GameUtils.readConfigurationFile(level);
		this.duration = gameConfiguration.getTimeLimit() * gameConfiguration.getNumUpdatesPerSecond();
	}

	/**
	 * Creates all the objects and populates the game state.
	 * Attributes each player a Bomberman.
	 */
	public void populateGame() {
		Player[] characterOwners = new Player[players.size()];
		players.values().toArray(characterOwners);

		List<Drawing> fixedDrawings = new LinkedList<Drawing>();
		int playerCounter = 0;
		final char[][] map = gameState.getMap();

		for (int rowIdx = 0; rowIdx < map.length; rowIdx++) {
			for (int colIdx = 0; colIdx < map[rowIdx].length; colIdx++) {
				char character = map[rowIdx][colIdx];

				// the position will be right in the middle
				final Position pos = new Position(colIdx + 0.5f, rowIdx + 0.5f);
				if (character == State.DrawingType.OBSTACLE.toChar()) {
					gameState.addAgent(new Obstacle(pos));
				} else if (character == State.DrawingType.BOMBERMAN.toChar()) {
					// TODO The bombermans are represented in the map as numbers
					// implement that
					Agent bomberman = new Bomberman(pos, characterOwners[playerCounter].getController(),
													 gameConfiguration.getbSpeed(),
													 gameConfiguration.getTimeBetweenBombs(),
													 gameConfiguration.getExplosionRange(),
													 gameConfiguration.getExplosionDuration());
					gameState.addAgent(bomberman);
					characterOwners[playerCounter].setAgent(bomberman);
				} else if (character == State.DrawingType.ROBOT.toChar()) {
					gameState.addAgent(new Robot(pos, gameConfiguration.getrSpeed()));
				} else if (character == State.DrawingType.WALL.toChar()) {
					fixedDrawings.add(new WallDrawing(new Position(colIdx, rowIdx)));
				}
			}
		}

		gameConfiguration.addFixedDrawings(fixedDrawings);
	}

	/**
	 * @return the number of updates per second
	 */
	public int getNumberUpdates() {
		return this.gameConfiguration.getNumUpdatesPerSecond();
	}

	public int getMapWidth() {
		return this.gameConfiguration.getMapWidth();
	}

	public int getMapHeight() {
		return this.gameConfiguration.getMapHeight();
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
	 * Pauses the game for the player with the given username
	 *
	 * @param username the player's username
	 */
	public void pause(String username) {
		gameState.pauseCharacter(players.get(username));
		pausePlayer(username);
	}

	/**
	 * Unpauses the game for the player with the given username
	 * @param username the player's username
	 */
	public void unpause(String username) {
		gameState.unPauseCharacter(playersOnPause.get(username));
		unpausePlayer(username);
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
			this.gameState.startCountingNow();
		}

		// Update the state
		//gameState.playAll();
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
			System.out.println("Game#updatePlayers: error creating json message.");
		}

		// Update every player with the character positions
		for (Player p : players.values()) {
			p.onUpdate(msg.toString());
		}
	}

	/**
	 * Verifies if the game has already finished.
	 * A game finishes when the game duration reaches 0 or when only a player is alive.
	 */
	public boolean hasFinished() {
		if (duration == 0) {
			return true;
		}

		// check how many are still alive
		int numMovableAgents = 0;
		final char[][] map = gameState.getMap();
		for (char[] line : map) {
			for (char c : line) {
				if (c == State.DrawingType.BOMBERMAN.toChar() ||
					c == State.DrawingType.ROBOT.toChar()) {
					numMovableAgents++;
				}
			}
		}

		// the game only finishes when only ONE movable agent is alive
		return numMovableAgents == 1;
	}
}
