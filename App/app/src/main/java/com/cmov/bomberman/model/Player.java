package com.cmov.bomberman.model;

import android.graphics.Canvas;
import android.util.JsonReader;
import com.cmov.bomberman.controller.GameView;
import com.cmov.bomberman.model.agent.Controllable;
import com.cmov.bomberman.model.drawing.BombermanDrawing;
import com.cmov.bomberman.model.drawing.Drawing;
import com.cmov.bomberman.model.drawing.ObstacleDrawing;

import java.io.IOException;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;

public class Player {
	private GameView gameView;

	private String username;
	private int currentScore;
	private Screen myScreen;
	private Controllable controller;

	public Player(String username, Controllable controller) {
		this.username = username;
		this.controller = controller;
		this.myScreen = new Screen();
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

	public void setUsername(String username) {
		this.username = username;
	}

	public int getCurrentScore() {
		return currentScore;
	}

	public void setCurrentScore(int currentScore) {
		this.currentScore = currentScore;
	}

	public Screen getMyScreen() {
		return myScreen;
	}

	// This method will create all the characters and all the drawables
	void onGameStart(GameConfiguration initialConfig) {
		myScreen.setFixedDrawings(initialConfig.getFixedDrawings());
	}

	// This method will be called when the game finishes. This can be useful to
	// tell each player who is the winner. (if there's any winner)
	void onGameEnd(GameConfiguration finalConfig) {
		//TODO:Implement this
	}

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

	// this method will receive text containing the info needed to construct
	// new drawings to be drawn on the client side
	// signature must be changed
	void onUpdate(String msg) {
		List<Drawing> drawings = parseMessage(msg);
		myScreen.setObjects(drawings);

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
