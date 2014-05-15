package com.cmov.bomberman.model;

import android.util.JsonReader;
import android.util.Log;
import com.cmov.bomberman.model.agent.Algorithm;
import com.cmov.bomberman.model.agent.Controllable;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.Map;

public class PlayerImpl implements Player {
	private final String TAG = this.getClass().getSimpleName();

	private final Screen screen;
	private final Controllable controller;
	private PlayerActionListener actionListener;

	/**
	 * Information to be shown in the views
	 */
	private int agentId;
	private int score;
	private int timeLeft;
	private int numPlayers;
	private boolean isDead;

	public PlayerImpl(Controllable controller, Screen screen) {
		this.controller = controller;
		this.screen = screen;
	}

	public void setPlayerActionListener(PlayerActionListener listener) {
		this.actionListener = listener;
	}

	public Algorithm getController() {
		return controller;
	}

	/**
	 * This method is called when the game starts.
	 * It initializes the map with all the walls
	 *
	 * @param wallPositions the position of every wall
	 */
	public void onGameStart(final int level, final List<Position> wallPositions) {
		if (actionListener != null) {
			GameConfiguration config = GameUtils.getInstance().readConfigurationFile(level);
			actionListener.onMapSizeChange(config.getMapWidth(), config.getMapHeight());
		}

		for (Position pos : wallPositions) {
			screen.createWallDrawing(pos);
		}
	}

	/**
	 * This method is called when the game finishes.
	 * Receives information about the game (like scores).
	 *
	 * @param scores the final score of each player
	 */

	public void onGameEnd(final Map<String, Integer> scores) {
		if (actionListener != null) {
			actionListener.onFinalScores(scores);
		}
	}

	/**
	 * Updates the drawings from the json report sent by the game.
	 * The fixed drawings are not included in the report.
	 *
	 * @param msg the json report.
	 */
	private void updateState(String msg) {
		JsonReader rd = new JsonReader(new StringReader(msg));

		try {
			rd.beginObject();
			while (rd.hasNext()) {
				String name = rd.nextName();
				if (name != null) {
					if (name.equals("AgentId")) {
						parseAgentId(rd);
					} else if (name.equals("Score")) {
						parseScore(rd);
					} else if (name.equals("TimeLeft")) {
						parseTimeLeft(rd);
					} else if (name.equals("NumPlayers")) {
						parseNumPlayers(rd);
					} else if (name.equals("Agents")) {
						parseAgents(rd);
					} else if (name.equals("Dead")) {
						parseDeath(rd);
					}
				}
			}
			rd.endObject();
		}
		catch (IOException e) {
			Log.e(TAG, "Error while parsing the message");
		}
	}

	private void parseAgentId(final JsonReader rd) throws IOException { this.agentId = rd.nextInt(); }

	private void parseScore(final JsonReader rd) throws IOException {
		this.score = rd.nextInt();
	}

	private void parseTimeLeft(final JsonReader rd) throws IOException {
		this.timeLeft = rd.nextInt();
	}

	private void parseNumPlayers(final JsonReader rd) throws IOException {
		this.numPlayers = rd.nextInt();
	}

	private void parseAgents(final JsonReader rd) throws IOException {
		rd.beginArray();
		while (rd.hasNext()) {
			parseAgent(rd);
		}
		rd.endArray();
	}

	private void parseAgent(final JsonReader rd) throws IOException {
		Position pos = null;
		int step = 0;
		int lastStep = 0;
		String type = "";
		String currentAction = "";
		String lastAction = "";
		int rangeRight = 0;
		int rangeLeft = 0;
		int rangeUp = 0;
		int rangeDown = 0;
		int drawingId = 0;
		boolean isDestroyed = false;

		// Parse object
		rd.beginObject();
		while (rd.hasNext()) {
			String name = rd.nextName();
			if (name != null) {
				if (name.equals("type")) {
					type = rd.nextString();
				} else if (name.equals("currentAction")) {
					currentAction = rd.nextString();
				} else if (name.equals("lastAction")) {
					lastAction = rd.nextString();
				} else if (name.equals("step")) {
					step = rd.nextInt();
				} else if (name.equals("lastStep")) {
					lastStep = rd.nextInt();
				} else if (name.equals("position")) {
					float x, y;
					rd.beginArray();
					x = (float) rd.nextDouble();
					y = (float) rd.nextDouble();
					pos = new Position(x, y);
					rd.endArray();
				} else if (name.equals("rangeRight")) {
					rangeRight = rd.nextInt();
				} else if (name.equals("rangeLeft")) {
					rangeLeft = rd.nextInt();
				} else if (name.equals("rangeUp")) {
					rangeUp = rd.nextInt();
				} else if (name.equals("rangeDown")) {
					rangeDown = rd.nextInt();
				} else if (name.equals("id")) {
					drawingId = rd.nextInt();
				} else if (name.equals("isDestroyed")) {
					isDestroyed = rd.nextBoolean();
				}
			}
		}
		rd.endObject();

		// create / update drawing
		if (screen.hasDrawing(drawingId)) {
			screen.updateDrawing(type, drawingId, pos, currentAction, lastAction, step, lastStep, rangeRight,
								 rangeLeft,
								 rangeUp, rangeDown, isDestroyed);
		} else {
			screen.createDrawing(drawingId == agentId, type, drawingId, pos, rangeRight, rangeLeft, rangeUp, rangeDown);
		}
	}

	private void parseDeath(final JsonReader rd) throws IOException {
		this.isDead = rd.nextBoolean();
	}

	/**
	 * This method will receive text containing the info needed to construct
	 * new drawings on the client side.
	 * Draws everything on the canvas.
	 * Alerts the user of losing the game if that's the case.
	 *
	 * @param msg the json report
	 */
	public void update(String msg) {
		updateState(msg);

		if (actionListener != null) {
			actionListener.draw();

			actionListener.onScoreChange(this.score);
			actionListener.onTimeChange(this.timeLeft);
			actionListener.onNumPlayersChange(this.numPlayers);

			if (isDead) {
				actionListener.onDeath();
			}
		}
	}
}
