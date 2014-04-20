package com.cmov.bomberman.model;

import android.util.JsonReader;

import java.io.IOException;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by JoãoEduardo on 15-04-2014.
 */
public class Player {

	private String username;
	private int currentScore;
	private Screen myScreen;
	private List<Agent> objects; // this is not needed
	private Controllable controller;

	public Player(String username, Controllable controller) {
		this.username = username;
		this.controller = controller;
	}

	public Controllable getController() {
		return controller;
	}

	public void addAgent(Agent agent) {
	    //objects.add(agent);
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

	public void setMyScreen(Screen myScreen) {
		this.myScreen = myScreen;
	}

	public List<Agent> getObjects() {
		return objects;
	}

	public void setObjects(List<Agent> objects) {
		this.objects = objects;
	}


	// This method will create all the characters and all the drawables
	void onGameStart(GameConfiguration initialConfig) {
		//TODO:Implement this
	}

	// This method will update all the state of the Player.
	// Ex: This will be called when any player pauses the game.
	void onGameUpdate(GameConfiguration currentConfig) {
		//TODO:Implement this
	}

	// This method will be called when the game finishes. This can be useful to
	// tell each player who is the winner. (if there's any winner)
	void onGameEnd(GameConfiguration finalConfig) {
		//TODO:Implement this
	}

    // this method will receive text containing the info needed to construct
    // new drawings to be drawn on the client side
    // signature must be changed
	void onUpdate(String msg) {
		List<Drawing> myDrawings = new LinkedList<Drawing>();

		JsonReader rd = new JsonReader(new StringReader(msg));
		try {
			rd.beginArray();
			while (rd.hasNext()) {
				Position position = null;
				int step = 0;
				String type = "";

				rd.beginObject();
				while (rd.hasNext()) {
					String name = rd.nextName();
					if (name != null) {
						if (name.equals("type")) {
							type = rd.nextString();
						} else if (name.equals("step")) {
							step = rd.nextInt();
						} else if (name.equals("position")) {
							float x, y;
							rd.beginArray();
							x = (float) rd.nextDouble();
							y = (float) rd.nextDouble();
							position = new Position(x, y);
							rd.endArray();
						}
					}

					if (type != null) {
						if (type.equals("Obstacle")) {
							myDrawings.add(new ObstacleDrawing(position, step));
						}
					}
				}
				rd.endObject();
			}
			rd.endArray();
		}
		catch (IOException e) {

		}

        // parse the given and construct objects
        // add them to the list
		myScreen.drawAll(myDrawings);
	}

}
