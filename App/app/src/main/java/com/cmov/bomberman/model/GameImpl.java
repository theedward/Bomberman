package com.cmov.bomberman.model;

import android.util.JsonWriter;
import android.util.Log;
import com.cmov.bomberman.model.agent.Agent;
import com.cmov.bomberman.model.agent.Bomberman;
import com.cmov.bomberman.model.agent.Obstacle;
import com.cmov.bomberman.model.agent.Robot;

import java.io.IOException;
import java.io.StringWriter;
import java.util.*;

/**
 * This is where all the game will be processed.
 * TODO remove synchronized and put smaller region locks
 */
public final class GameImpl implements Game {
	private final String TAG = this.getClass().getSimpleName();
    private final Map<String, Player> players;
    private final Map<String, Player> playersOnPause;
	private final Map<String, Bomberman> playersAgent;
	private final int bombermanIds[];
	private final Position bombermanPos[];
    private final State gameState;
    private final GameConfiguration gameConfiguration;
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
    public GameImpl(final int level) {
		this.isPaused = false;
        this.players = new HashMap<String, Player>();
        this.playersOnPause = new HashMap<String, Player>();
		this.playersAgent = new HashMap<String, Bomberman>();
        this.gameState = new State();

        // Read data from files
        this.gameState.setMap(GameUtils.readLevelFromFile(level));
        this.gameConfiguration = GameUtils.readConfigurationFile(level);
        this.numRoundsLeft = gameConfiguration.getTimeLimit() * gameConfiguration.getNumUpdatesPerSecond();

		this.bombermanPos = new Position[gameConfiguration.getMaxNumPlayers()];
		this.bombermanIds = new int[gameConfiguration.getMaxNumPlayers()];
		populateGame();
    }

    /**
     * Creates all the objects and populates the game state.
     * Attributes each player a Bomberman.
     */
    private void populateGame() {
		final String[] usernames = new String[players.size()];
        final PlayerImpl[] characterOwners = new PlayerImpl[players.size()];
		players.keySet().toArray(usernames);
        players.values().toArray(characterOwners);

		wallPositions = new LinkedList<Position>();

		int idCounter = 0;
		final char[][] map = gameState.getMap();
        for (int rowIdx = 0; rowIdx < map.length; rowIdx++) {
            for (int colIdx = 0; colIdx < map[rowIdx].length; colIdx++) {
				// the position will be right in the middle
				final Position pos = new Position(colIdx + Agent.HEIGHT/2, rowIdx + Agent.WIDTH/2);
				final char character = map[rowIdx][colIdx];

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
						final int bombermanId = Integer.parseInt(Character.toString(character)) - 1;

						// save bomberman position for later use
						bombermanPos[bombermanId] = pos;
						bombermanIds[bombermanId] = idCounter;
						map[rowIdx][colIdx] = State.DrawingType.EMPTY.toChar();
						idCounter++;
					} catch (NumberFormatException e) {
						// Not a Bomberman
					}
				}
			}
		}

        // must pass counter id to game state for posterior object creations, such as bombs
        gameState.setLastId(idCounter);
    }

	public void start() {
		this.begin();

		final int timeSleep = 1000 / gameConfiguration.getNumUpdatesPerSecond();
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

				Log.v(TAG, "Updating...");

				final long now = System.currentTimeMillis();
				update(now - lastTime);
				lastTime = now;

				final long dt = System.currentTimeMillis() - now;
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

	@Override
	public Collection<String> getPlayerUsernames() {
		Set<String> usernames = new LinkedHashSet<String>();
		usernames.addAll(players.keySet());
		usernames.addAll(playersOnPause.keySet());
		return usernames;
	}

	public int getMapWidth() {
        return this.gameConfiguration.getMapWidth();
    }

    public int getMapHeight() {
        return this.gameConfiguration.getMapHeight();
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

        gameState.unpauseAgent(playersAgent.get(username));
    }

	/**
	 * A player leaves the game.
	 * @param username the player's username
	 */
	public synchronized void quit(String username) {
		// TODO
	}

	/**
	 * A player joins the game.
	 * @param username the player's username
	 */
	public synchronized void join(String username, Player player) {
		if (playersAgent.size() < gameConfiguration.getMaxNumPlayers()) {
			players.put(username, player);

			// The next bomberman to be chosen
			final int bombermanId = playersAgent.size();
			final int agentId = bombermanIds[bombermanId];
			final Position pos = bombermanPos[bombermanId];
			final char[][] map = gameState.getMap();
			map[pos.yToDiscrete()][pos.xToDiscrete()] = State.DrawingType.BOMBERMAN.toChar();
			Bomberman bomberman = new Bomberman(pos, player.getController(), agentId,
												gameConfiguration.getbSpeed(), gameConfiguration.getTimeBetweenBombs(),
												gameConfiguration.getExplosionRange(),
												gameConfiguration.getExplosionDuration(),
												gameConfiguration.getPointRobot(),
												gameConfiguration.getPointOpponent());
			gameState.addAgent(bomberman);
			player.setAgentId(agentId);
			playersAgent.put(username, bomberman);
		}
	}

    /**
     * Creates all the agents in the map.
     * Calls Player#onGameStart for every registered player.
     * Starts the game loop
     */
    private synchronized void begin() {
        Log.i(TAG, "Game has started");

        for (Player p : players.values()) {
            p.onGameStart(wallPositions);
        }
    }

	/**
	 * Calls method onGameEnd of every player. It sends the final scores of the game.
	 */
    private synchronized void end() {
		Log.i(TAG, "Game has ended.");

        for (Player p : players.values()) {
            p.onGameEnd(checkScores());
        }
    }

    private Map<String, Integer> checkScores() {
        final Map<String, Integer> scores = new TreeMap<String, Integer>();
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
		final long timeBeforePlay = System.currentTimeMillis();
        gameState.playAll(dt);
		Log.i(TAG, "Playing took " + (System.currentTimeMillis() - timeBeforePlay) + " msec.");

		final long timeBeforeUpdate = System.currentTimeMillis();
		updatePlayers();
		Log.i(TAG, "Updating players took " + (System.currentTimeMillis() - timeBeforeUpdate) + " msec.");

		// remove agents after update
        gameState.removeDestroyedAgents();
        numRoundsLeft--;

        if (this.hasFinished()) {
            this.end();
        }
    }

    /**
     * Calls Player#update with the new state.
     */
    private void updatePlayers() {
        // Get all the character positions
        final StringWriter msg = new StringWriter();
        final JsonWriter writer = new JsonWriter(msg);

        // Update every player with the character positions
        for (Map.Entry<String, Player> entry : players.entrySet()) {
			try {
				writer.setIndent("  ");
				writer.beginObject();

				createScoreMsg(writer, entry.getKey());
				createTimeMsg(writer);
				createNumPlayersMsg(writer);
				createAgentsMsg(writer);

				writer.endObject();
				writer.close();
			} catch (IOException e) {
				Log.e(TAG, "Error creating json message.");
			}

            entry.getValue().update(msg.toString());
        }
    }

	private void createScoreMsg(final JsonWriter wr, final String username) throws IOException {
		final Bomberman playerAgent = playersAgent.get(username);
		wr.name("Score").value(playerAgent.getScore());
	}

	private void createTimeMsg(final JsonWriter wr) throws IOException {
		final int timeLeft = this.numRoundsLeft / this.gameConfiguration.getNumUpdatesPerSecond();
		wr.name("TimeLeft").value(timeLeft);
	}

	private void createNumPlayersMsg(final JsonWriter wr) throws IOException {
		final int numPlayers = players.size() + playersOnPause.size();
		wr.name("NumPlayers").value(numPlayers);
	}

	private void createAgentsMsg(final JsonWriter wr) throws IOException {
		wr.name("Agents");
		wr.beginArray();
		for (Agent object : gameState.getObjects()) {
			object.toJson(wr);
		}
		wr.endArray();
	}

    /**
     * Verifies if the game has already finished.
     * A game finishes when the game numRoundsLeft reaches 0, when only a player is alive and the other robots and players
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
