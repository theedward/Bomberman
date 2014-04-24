package com.cmov.bomberman.model;

import android.graphics.Canvas;
import android.util.JsonReader;
import com.cmov.bomberman.controller.GameView;
import com.cmov.bomberman.model.agent.Agent;
import com.cmov.bomberman.model.agent.Controllable;
import com.cmov.bomberman.model.drawing.BombermanDrawing;
import com.cmov.bomberman.model.drawing.Drawing;
import com.cmov.bomberman.model.drawing.ObstacleDrawing;

import java.io.IOException;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;

public class Player {
	private final String username;
	private final Screen screen;
	private final Controllable controller;
	private Agent agent;

	/**
	 * This is needed to draw in the canvas in a synchronized manner.
	 */
	private GameView gameView;
	private int score;

	public Player(String username, Controllable controller) {
		this.username = username;
		this.controller = controller;
		this.screen = new Screen();
		this.gameView = null;
		this.score = 0;
	}

	public Agent getAgent() {
		return agent;
	}

	public void setAgent(final Agent agent) {
		this.agent = agent;
	}

	public void setGameView(final GameView gameView) {
		this.gameView = gameView;
	}

	public Controllable getController() {
		return controller;
	}

	public String getUsername() {
		return username;
	}

	public int getScore() {
		return score;
	}

	public void addScore(final int amount) {
		this.score += amount;
	}

	public Screen getScreen() {
		return screen;
	}

	/**
	 * This method is called when the game starts.
	 * It adds all the fixed drawings to the screen. All the other drawings are added on Player#onUpdate.
	 * @param initialConfig the initial game configuration
	 */
	void onGameStart(GameConfiguration initialConfig) {
		screen.setFixedDrawings(initialConfig.getFixedDrawings());
	}

	// This method will be called when the game finishes.

	/**
	 * This method is called when the game finishes.
	 * This can be useful to tell each player who is the winner. (if there's any winner)
	 * @param finalConfig the final game configuration
	 */
	void onGameEnd(GameConfiguration finalConfig) {
		// TODO
	}

	/**
	 * Creates all the drawings from the json report sent by the game.
	 * The fixed drawings are not included in the report.
	 *
	 * @param msg the json report.
	 * @return the drawings existent in the game state.
	 */
	private List<Drawing> parseMessage(String msg) {
		List<Drawing> drawings = new LinkedList<Drawing>();
		JsonReader rd = new JsonReader(new StringReader(msg));

		try {
			rd.beginArray();
			while (rd.hasNext()) {
				Position position = null;
				int step = 0;
				String type = "";
				String currentAction = "";

				// Parse object
				rd.beginObject();
				while (rd.hasNext()) {
					String name = rd.nextName();
					if (name != null) {
						if (name.equals("type")) {
							type = rd.nextString();
						} else if (name.equals("currentAction")) {
							currentAction = rd.nextString();
						} else if (name.equals("step")) {
							step = rd.nextInt();
						} else if (name.equals("position")) {
							float x, y;
							rd.beginArray();
							x = (float) rd.nextDouble();
							y = (float) rd.nextDouble();
							position = new Position(x * GameUtils.IMG_WIDTH, y * GameUtils.IMG_HEIGHT);
							rd.endArray();
						}
					}
				}
				rd.endObject();

				// Create object instance
				if (type != null) {
					if (type.equals("Obstacle")) {
						drawings.add(new ObstacleDrawing(position, step));
					} else if (type.equals("Bomberman")) {
						drawings.add(new BombermanDrawing(position, step, currentAction));
					} else if (type.equals("Robot")) {
						// TODO
					} else if (type.equals("Bomb")) {
						// TODO
					}
				}
			}
			rd.endArray();
		}
		catch (IOException e) {
			System.out.println("Player#onUpdate: Error while parsing the message.");
		}

		return drawings;
	}

	/**
	 * This method will receive text containing the info needed to construct
	 * new drawings on the client side.
	 * It also draws everything on the canvas.
	 * @param msg the json report
	 */
	void onUpdate(String msg) {
		List<Drawing> drawings = parseMessage(msg);
		screen.setObjects(drawings);

		Canvas canvas = null;
		try {
			if (gameView.getHolder() != null) {
				canvas = gameView.getHolder().lockCanvas();
				synchronized (gameView.getHolder()) {
					gameView.onDraw(canvas);
				}
			}
		} finally {
			if (canvas != null) {
				gameView.getHolder().unlockCanvasAndPost(canvas);
			}
		}
	}

}
