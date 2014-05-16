package com.cmov.bomberman.model;

import android.util.JsonWriter;
import android.util.Log;
import com.cmov.bomberman.model.agent.Agent;
import com.cmov.bomberman.model.agent.Bomberman;
import com.cmov.bomberman.model.agent.Obstacle;
import com.cmov.bomberman.model.agent.Robot;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.*;

/**
 * This is where all the game will be processed.
 */
public class GameImpl implements Game, Serializable {
	private String TAG = this.getClass().getSimpleName();

	private int level;
	private Map<String, Player> players;
	private Map<String, Player> playersOnPause;
	private Map<String, Bomberman> playersAgent;
	private Map<String, Integer> playerAgentIdx;
	private State gameState;
	private int bombermanIds[];
	private Position bombermanPos[];
	private boolean bombermanUsed[];
	private GameConfiguration gameConfiguration;

	private boolean started;
	private boolean isPaused;
	private List<Position> wallPositions;
	/**
	 * The number of updates the game will have.
	 */
	private int numRoundsLeft;

	/**
	 * This constructor performs all the necessary steps to start the game right next.
	 * However, the game would be without any player.
	 *
	 * @param level the level to be played in this game
	 */
	public GameImpl(int level) {
		this.level = level;
		this.isPaused = false;
		this.players = new HashMap<String, Player>();
		this.playersOnPause = new HashMap<String, Player>();
		this.playersAgent = new HashMap<String, Bomberman>();
		this.playerAgentIdx = new HashMap<String, Integer>();
		this.gameState = new State();

		// Read data from files
		this.gameState.setMap(GameUtils.getInstance().readLevelFromFile(level));
		this.gameConfiguration = GameUtils.getInstance().readConfigurationFile(level);
		this.numRoundsLeft = gameConfiguration.getTimeLimit() * gameConfiguration.getNumUpdatesPerSecond();

		this.bombermanPos = new Position[gameConfiguration.getMaxNumPlayers()];
		this.bombermanIds = new int[gameConfiguration.getMaxNumPlayers()];
		this.bombermanUsed = new boolean[gameConfiguration.getMaxNumPlayers()];
		populateGame();
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(final int level) {
		this.level = level;
	}

	/**
	 * Creates all the objects and populates the game state.
	 * Attributes each player a Bomberman.
	 */
	private void populateGame() {
		String[] usernames = new String[players.size()];
		PlayerImpl[] characterOwners = new PlayerImpl[players.size()];
		players.keySet().toArray(usernames);
		players.values().toArray(characterOwners);

		wallPositions = new LinkedList<Position>();

		int idCounter = 0;
		char[][] map = gameState.getMap();
		for (int rowIdx = 0; rowIdx < map.length; rowIdx++) {
			for (int colIdx = 0; colIdx < map[rowIdx].length; colIdx++) {
				// the position will be right in the middle
				Position pos = new Position(colIdx + Agent.HEIGHT / 2, rowIdx + Agent.WIDTH / 2);
				char character = map[rowIdx][colIdx];

				if (character == State.DrawingType.OBSTACLE.toChar()) {
					gameState.addAgent(new Obstacle(pos, idCounter));
					idCounter++;
				} else if (character == State.DrawingType.ROBOT.toChar()) {
					gameState.addAgent(new Robot(pos, idCounter, gameConfiguration.getrSpeed()));
					idCounter++;
				} else if (character == State.DrawingType.WALL.toChar()) {
					wallPositions.add(new Position(colIdx, rowIdx));
				} else {
					// Let's see if it's a Bomberman
					try {
						// starts at 1
						int bombermanId = Integer.parseInt(Character.toString(character)) - 1;

						// save bomberman position for later use
						bombermanPos[bombermanId] = pos;
						bombermanIds[bombermanId] = idCounter;
						map[rowIdx][colIdx] = State.DrawingType.EMPTY.toChar();
						idCounter++;
					}
					catch (NumberFormatException e) {
						// Not a Bomberman
					}
				}
			}
		}

		// must pass counter id to game state for posterior object creations, such as bombs
		gameState.setLastId(idCounter);
	}

	public void start() {
		Log.i(TAG, "Game has started");

		begin();
		gameLoop();
		end();

		Log.i(TAG, "Game has ended");
	}

	private void gameLoop() {
		int timeSleep = 1000 / gameConfiguration.getNumUpdatesPerSecond();
		long lastTime = System.currentTimeMillis();
		while (!hasFinished()) {
			try {
				// when the game is paused, just wait until an unpause
				synchronized (this) {
					if (isPaused) {
						this.wait();

						// time spent on pause doesn't count
						lastTime = System.currentTimeMillis();
					}
				}

				Log.v(TAG, "Update:");

				long now = System.currentTimeMillis();
				update(now - lastTime);
				lastTime = now;

				long dt = System.currentTimeMillis() - now;
				// suspend thread only when the time spent on update is smaller than the time it should
				// spend on each update (1000 / numUpdates).
				if (timeSleep > dt) {
					Thread.sleep(timeSleep - dt);
				}
			}
			catch (InterruptedException e) {
				return;
			}
		}
	}

	@Override
	public void pause() {
		isPaused = true;
	}

	@Override
	public void unpause() {
		isPaused = false;
		synchronized (this) {
			notify();
		}
	}

	/**
	 * Pauses the game for the player with the given username
	 *
	 * @param username the player's username
	 */
	public synchronized void pause(String username) {
		Player p = players.get(username);
		playersOnPause.put(username, p);
		players.remove(username);

		if (players.size() == 0) {
			pause();
		}

		gameState.pauseAgent(playersAgent.get(username));
	}

	/**
	 * Unpauses the game for the player with the given username
	 *
	 * @param username the player's username
	 */
	public synchronized void unpause(String username) {
		Player p = playersOnPause.get(username);
		playersOnPause.remove(username);
		players.put(username, p);

		if (players.size() == 1) {
			unpause();
		}

		gameState.unpauseAgent(playersAgent.get(username));
	}

	/**
	 * A player leaves the game.
	 *
	 * @param username the player's username
	 */
	public synchronized void quit(String username) {
		if (players.containsKey(username)) {
			players.remove(username);
		}

		if (playersOnPause.containsKey(username)) {
			playersOnPause.remove(username);
		}

		if (playersAgent.containsKey(username)) {
			gameState.destroyAgent(playersAgent.get(username));
			playersAgent.remove(username);
		}

		if (playerAgentIdx.containsKey(username)) {
			int idx = playerAgentIdx.get(username);
			bombermanUsed[idx] = false;
			playerAgentIdx.remove(username);
		}

		if (players.isEmpty() && playersOnPause.isEmpty()) {
			Log.i(TAG, "Unlocking possible paused game");
			notify();
		}
	}

	/**
	 * A player joins the game.
	 *
	 * @param username the player's username
	 */
	public synchronized void join(String username, Player player) {
		if (!players.containsKey(username)) {
			if (playersAgent.size() < gameConfiguration.getMaxNumPlayers()) {
				players.put(username, player);

				// Selecting the bomberman to be used by this player
				int bombermanIdx = 0;
				for (int i = 0; i < bombermanUsed.length; i++) {
					if (!bombermanUsed[i]) {
						bombermanIdx = i;
						bombermanUsed[i] = true;
						playerAgentIdx.put(username, i);
						break;
					}
				}

				int bombermanId = bombermanIds[bombermanIdx];
				Position pos = bombermanPos[bombermanIdx];
				char[][] map = gameState.getMap();
				map[pos.yToDiscrete()][pos.xToDiscrete()] = State.DrawingType.BOMBERMAN.toChar();
				Bomberman bomberman = new Bomberman(pos, player.getController(), bombermanId,
													gameConfiguration.getbSpeed(),
													gameConfiguration.getExplosionDuration(),
													gameConfiguration.getTimeBetweenBombs(),
													gameConfiguration.getExplosionRange(),
													gameConfiguration.getTimeToExplode(),
													gameConfiguration.getPointRobot(),
													gameConfiguration.getPointOpponent());
				gameState.addAgent(bomberman);
				playersAgent.put(username, bomberman);

				// Happens when the player joins during the game
				if (started) {
					player.onGameStart(level, wallPositions);
				}
			}
		} else {
			if (playersAgent.containsKey(username)) {
				// reconnect controllable to agent
				Bomberman playerAgent = playersAgent.get(username);
				playerAgent.setAlgorithm(player.getController());
			}
		}
	}

	/**
	 * Creates all the agents in the map.
	 * Calls Player#onGameStart for every registered player.
	 * Starts the game loop
	 */
	private synchronized void begin() {
		this.started = true;
		for (Player p : players.values()) {
			p.onGameStart(level, wallPositions);
		}
	}

	/**
	 * Calls method onGameEnd of every player. It sends the scores of the game.
	 */
	private synchronized void end() {
		for (Player p : players.values()) {
			p.onGameEnd(checkScores());
		}
	}

	private Map<String, Integer> checkScores() {
		Map<String, Integer> scores = new TreeMap<String, Integer>();
		for (Map.Entry<String, Bomberman> entry : playersAgent.entrySet()) {
			scores.put(entry.getKey(), entry.getValue().getScore());
		}

		return scores;
	}

	/**
	 * Updates the state (new frame).
	 */
	private synchronized void update(long dt) {
		// Update the state
		long timeBeforePlay = System.currentTimeMillis();
		gameState.playAll(dt);
		Log.v(TAG, "Playing took " + (System.currentTimeMillis() - timeBeforePlay) + " msec.");

		long timeBeforeUpdate = System.currentTimeMillis();
		updatePlayers();
		Log.v(TAG, "Updating players took " + (System.currentTimeMillis() - timeBeforeUpdate) + " msec.");

		// remove agents after update
		gameState.removeDestroyedAgents();
		numRoundsLeft--;
	}

	/**
	 * Calls Player#update with the new state.
	 */
	private void updatePlayers() {
		// Update every player with the character positions
		for (Map.Entry<String, Player> entry : players.entrySet()) {
			// Get all the character positions
			StringWriter msg = new StringWriter();
			JsonWriter writer = new JsonWriter(msg);

			try {
				writer.setIndent("  ");
				writer.beginObject();

				createAgentIdMsg(writer, entry.getKey());
				createScoreMsg(writer, entry.getKey());
				createTimeMsg(writer);
				createNumPlayersMsg(writer);
				createAgentsMsg(writer);
				createDeathMsg(writer, entry.getKey());

				writer.endObject();
				writer.close();
			}
			catch (IOException e) {
				Log.e(TAG, "Error creating json message.");
			}

			entry.getValue().update(msg.toString());
		}
	}

	private void createAgentIdMsg(JsonWriter wr, String username) throws IOException {
		Bomberman playerAgent = playersAgent.get(username);
		wr.name("AgentId").value(playerAgent.getId());
	}

	private void createScoreMsg(JsonWriter wr, String username) throws IOException {
		Bomberman playerAgent = playersAgent.get(username);
		wr.name("Score").value(playerAgent.getScore());
	}

	private void createTimeMsg(JsonWriter wr) throws IOException {
		int timeLeft = this.numRoundsLeft / this.gameConfiguration.getNumUpdatesPerSecond();
		wr.name("TimeLeft").value(timeLeft);
	}

	private void createNumPlayersMsg(JsonWriter wr) throws IOException {
		int numPlayers = players.size() + playersOnPause.size();
		wr.name("NumPlayers").value(numPlayers);
	}

	private void createAgentsMsg(JsonWriter wr) throws IOException {
		wr.name("Agents");
		wr.beginArray();
		for (Agent object : gameState.getObjects()) {
			object.toJson(wr);
		}
		wr.endArray();
	}

	private void createDeathMsg(JsonWriter wr, String username) throws IOException {
		wr.name("Dead").value(playersAgent.get(username).isDestroyed());
	}

	/**
	 * Verifies if the game has already finished.
	 * A game finishes when the game numRoundsLeft reaches 0, when only a player is alive and the other robots and
	 * players
	 * are dead or when all players are dead and some robots still exist.
	 */
	private synchronized boolean hasFinished() {
		if (numRoundsLeft == 0) {
			return true;
		}

		// check how many are still alive
		int numBombermans = 0;
		int numRobots = 0;
		for (Agent agent : gameState.getObjects()) {
			if (agent.getType().equals("Bomberman")) {
				numBombermans++;
			} else if (agent.getType().equals("Robot")) {
				numRobots++;
			}
		}

		for (Agent agent : gameState.getPausedCharacters()) {
			if (agent.getType().equals("Bomberman")) {
				numBombermans++;
			} else if (agent.getType().equals("Robot")) {
				numRobots++;
			}
		}

		return (numBombermans == 0) || (numBombermans == 1 && numRobots == 0);
	}
}
