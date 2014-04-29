package com.cmov.bomberman.model;

import android.util.JsonWriter;
import com.cmov.bomberman.model.agent.Agent;
import com.cmov.bomberman.model.agent.Bomberman;
import com.cmov.bomberman.model.agent.Obstacle;
import com.cmov.bomberman.model.agent.Robot;
import com.cmov.bomberman.model.drawing.*;

import java.io.IOException;
import java.io.StringWriter;
import java.util.*;

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
    private int numRoundsLeft;
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
        this.numRoundsLeft = gameConfiguration.getTimeLimit() * gameConfiguration.getNumUpdatesPerSecond();
    }

    /**
     * Creates all the objects and populates the game state.
     * Attributes each player a Bomberman.
     */
    public void populateGame() {
        Player[] characterOwners = new Player[players.size()];
        players.values().toArray(characterOwners);

        List<WallDrawing> wallDrawings = new LinkedList<WallDrawing>();
        Map<Integer, Drawing> drawings = new HashMap<Integer, Drawing>();
        int idDrawings = 0;
        final char[][] map = gameState.getMap();

        for (int rowIdx = 0; rowIdx < map.length; rowIdx++) {
            for (int colIdx = 0; colIdx < map[rowIdx].length; colIdx++) {
                char character = map[rowIdx][colIdx];
                // the position will be right in the middle
                final Position pos = new Position(colIdx + 0.5f, rowIdx + 0.5f);
                if (character == State.DrawingType.OBSTACLE.toChar()) {
                    gameState.addAgent(new Obstacle(pos, idDrawings));
                    drawings.put(idDrawings, new ObstacleDrawing(new Position(colIdx, rowIdx), 0));
                    idDrawings++;
                } else if (character == State.DrawingType.ROBOT.toChar()) {
                    gameState.addAgent(new Robot(pos, idDrawings, gameConfiguration.getrSpeed()));
                    drawings.put(idDrawings, new RobotDrawing(new Position(colIdx, rowIdx), 0, ""));
                    idDrawings++;
                } else if (character == State.DrawingType.WALL.toChar()) {
                    wallDrawings.add(new WallDrawing(new Position(colIdx, rowIdx)));
                } else {
                    // Let's see if it's a Bomberman
                    try {
                        // starts at 1
                        int bombermanId = Integer.parseInt(Character.toString(character)) - 1;
                        // the number of players must be greater or equal than the number of this bomberman
                        if (bombermanId < characterOwners.length) {
                            map[rowIdx][colIdx] = State.DrawingType.BOMBERMAN.toChar();
                            Agent bomberman = new Bomberman(pos, characterOwners[bombermanId].getController(),
                                    idDrawings, gameConfiguration.getbSpeed(),
                                    gameConfiguration.getTimeBetweenBombs(),
                                    gameConfiguration.getExplosionRange(),
                                    gameConfiguration.getExplosionDuration(),
                                    gameConfiguration.getPointRobot(),
                                    gameConfiguration.getPointOpponent());
                            gameState.addAgent(bomberman);
                            characterOwners[bombermanId].setAgent(bomberman);
                            drawings.put(idDrawings, new BombermanDrawing(new Position(colIdx, rowIdx), 0, ""));
                            idDrawings++;
                        } else {
                            map[rowIdx][colIdx] = State.DrawingType.EMPTY.toChar();
                        }
                    } catch (NumberFormatException e) {
                        // Not a Bomberman
                    }
                }
            }
        }

        gameConfiguration.setWallDrawings(wallDrawings);
        gameConfiguration.setMutableDrawings(drawings);
        // must pass counter id to game state for posterior object creations, such as bombs
        gameState.setObjectsIdCounter(idDrawings);
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
    public synchronized void pause(String username) {
        gameState.pauseAgent(players.get(username));
        pausePlayer(username);
    }

    /**
     * Unpauses the game for the player with the given username
     *
     * @param username the player's username
     */
    public synchronized void unpause(String username) {
        gameState.unpauseAgent(playersOnPause.get(username));
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
        System.out.println("Telling the game to end");
        // tell players that the game has ended
        for (Player p : players.values()) {
            p.onGameEnd(gameConfiguration, checkScores());
        }

    }

    public TreeMap<String, Integer> checkScores() {
        TreeMap<String, Integer> scores = new TreeMap<String, Integer>();
        for (Player p : players.values()) {
            scores.put(p.getUsername(), p.getCurrentScore());
        }
        return scores;

    }

    /**
     * Updates the state (new frame).
     */
    public synchronized void update() {
        if (!this.hasStarted) {
            this.hasStarted = true;
            this.begin();
            this.gameState.startCountingNow();
        }

        // Update the state
        gameState.playAll();
        updatePlayers();

        // the destroyed flag must be sent on the Player#onUpdate
        gameState.removeDestroyedAgents();

        this.numRoundsLeft--;

        if (this.hasFinished()) {
            this.end();
        }
    }

    /**
     * Calls Player#onUpdate with the new state.
     */
    private synchronized void updatePlayers() {
        // Get all the character positions
        StringWriter msg = new StringWriter();
        JsonWriter writer = new JsonWriter(msg);

        // Update every player with the character positions
        for (Player p : players.values()) {
			try {
				writer.setIndent("  ");
				writer.beginObject();

				createScoreMsg(writer, p);
				createTimeMsg(writer);
				createAgentsMsg(writer);

				writer.endObject();
				writer.close();
			} catch (IOException e) {
				System.out.println("Game#updatePlayers: error creating json message.");
			}

            p.onUpdate(msg.toString());
        }
    }

	private void createScoreMsg(final JsonWriter wr, final Player player) throws IOException {
		final Bomberman playerAgent = (Bomberman) player.getAgent();
		wr.name("Score").value(playerAgent.getScore());
	}

	private void createTimeMsg(final JsonWriter wr) throws IOException {
		final int timeLeft = this.numRoundsLeft / this.gameConfiguration.getNumUpdatesPerSecond();
		wr.name("TimeLeft").value(timeLeft);
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
    public synchronized boolean hasFinished() {
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

    public synchronized boolean hasLost() {
        Player[] characterOwners = new Player[players.size()];
        players.values().toArray(characterOwners);
        for (Player player : characterOwners) {
            if (player.getAgent().isDestroyed())
                return true;
        }
        return false;

    }
}
