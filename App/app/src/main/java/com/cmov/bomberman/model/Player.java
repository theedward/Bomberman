package com.cmov.bomberman.model;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.util.JsonReader;
import com.cmov.bomberman.controller.GameActivity;
import com.cmov.bomberman.controller.GameView;
import com.cmov.bomberman.model.agent.Agent;
import com.cmov.bomberman.model.agent.Controllable;

import java.io.IOException;
import java.io.StringReader;
import java.util.TreeMap;

public class Player {
	private final String username;
	private final Screen screen;
	private final Controllable controller;
	private Agent agent;
	private int currentScore;
	private int currentTimeLeft;

	/**
	 * This is needed to draw in the canvas in a synchronized manner.
	 */
	private GameActivity gameActivity;

	public Player(String username, Controllable controller) {
		this.username = username;
		this.controller = controller;
		this.screen = new Screen();
		this.gameActivity = null;
		this.currentScore = 0;
		this.currentTimeLeft = 0;
	}

	public Agent getAgent() {
		return agent;
	}

	public void setAgent(final Agent agent) {
		this.agent = agent;
	}

	public void setGameView(final GameActivity gameActivity) {
		this.gameActivity = gameActivity;
	}

	public Controllable getController() {
		return controller;
	}

	public String getUsername() {
		return username;
	}

	public int getCurrentScore() {
		return currentScore;
	}

	public void addScore(final int amount) {
		this.currentScore += amount;
	}

	public Screen getScreen() {
		return screen;
	}

	/**
	 * This method is called when the game starts.
	 * It adds all the fixed drawings to the screen. All the other drawings are added on Player#onUpdate.
	 *
	 * @param initialConfig the initial game configuration
	 */
	void onGameStart(GameConfiguration initialConfig) {
		screen.setWallDrawings(initialConfig.getWallDrawings());
		screen.setDrawings(initialConfig.getMutableDrawings());
	}





	// This method will be called when the game finishes.

	/**
	 * This method is called when the game finishes.
	 * This can be useful to tell each player who is the winner. (if there's any winner)
	 *
	 * @param finalConfig the final game configuration
	 */

	void onGameEnd(GameConfiguration finalConfig, TreeMap<String, Integer> scores) {
        //Preparing the String for output
        System.out.println("Inside onGameEnd");
        gameActivity.callDialog(scores);
        }

                /**
                 * Updates creates or destroys the drawings from the json report sent by the game.
                 * The fixed drawings are not included in the report.
                 *
                 * @param msg the json report.
                 */

    private void parseMessage(String msg) {
		JsonReader rd = new JsonReader(new StringReader(msg));

		try {
			rd.beginObject();
			while (rd.hasNext()) {
				String name = rd.nextName();
				if (name != null) {
					if (name.equals("Score")) {
						parseScore(rd);
					} else if (name.equals("TimeLeft")) {
						parseTimeLeft(rd);
					} else if (name.equals("Agents")) {
						parseAgents(rd);
					}
				}
			}
		}
		catch (IOException e) {
			System.out.println("Player#onUpdate: Error while parsing the message.");
		}
	}

	private void parseScore(final JsonReader rd) throws IOException {
		this.currentScore = rd.nextInt();
	}

	private void parseTimeLeft(final JsonReader rd) throws IOException {
		this.currentTimeLeft = rd.nextInt();
	}

	private void parseAgents(final JsonReader rd) throws IOException {
		rd.beginArray();
		while (rd.hasNext()) {
			parseAgent(rd);
		}
		rd.endArray();
	}

	private void parseAgent(final JsonReader rd) throws IOException {
		Position position = null;
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
					position = new Position(x, y);
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

		// updates object
		if (type != null) {
			screen.updateDrawing(type, drawingId, position, currentAction, lastAction, step, lastStep,
								 rangeRight, rangeLeft, rangeUp, rangeDown, isDestroyed);
		}
	}

	/**
	 * This method will receive text containing the info needed to construct
	 * new drawings on the client side.
	 * It also draws everything on the canvas.
	 *
	 * @param msg the json report
	 */
	void onUpdate(String msg) {
		parseMessage(msg);

		// update canvas
		final GameView gameView = gameActivity.getGameView();
		Canvas canvas = null;
		try {
			if (gameView.getHolder() != null) {
				canvas = gameView.getHolder().lockCanvas();
				synchronized (gameView.getHolder()) {
					if (canvas != null) {
						gameView.onDraw(canvas);
					}
				}
			}
		} finally {
			if (canvas != null) {
				gameView.getHolder().unlockCanvasAndPost(canvas);
			}
		}

		// update other elements in the view
		gameActivity.updateScoreView(this.currentScore);
		gameActivity.updateTimeView(this.currentTimeLeft);
	}

}
