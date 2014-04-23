package com.cmov.bomberman.controller;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import com.cmov.bomberman.R;
import com.cmov.bomberman.model.Controllable;
import com.cmov.bomberman.model.Game;
import com.cmov.bomberman.model.Player;

import java.util.Map;
import java.util.TreeMap;

public class GameActivity extends Activity {
	private static final String DEFAULT_USERNAME = "Bomberman";
	private static final int DEFAULT_LEVEL = 1;
	private static final Map<Character, String> DEFAULT_KEYMAP = new TreeMap<Character, String>() {{
		put('U', "MOVE_TOP");
		put('L', "MOVE_LEFT");
		put('D', "MOVE_BOTTOM");
		put('R', "MOVE_RIGHT");
		put('B', "PUT_BOMB");
	}};

	private Game game;
	private Player player;
	private Controllable controller;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);

		// Get the level
		Bundle extras = getIntent().getExtras();
		Integer level = null;
		if (extras != null) {
			level = (Integer) extras.get("level");
		}
		if (level == null) {
			System.out.println("Invalid level...");
			level = DEFAULT_LEVEL;
		}

		// Initialize player
		GameView gameView = (GameView) findViewById(R.id.canvas);
		controller = new Controllable(DEFAULT_KEYMAP);
		player = new Player(DEFAULT_USERNAME, controller);
		player.setGameView(gameView);
		gameView.setScreen(player.getMyScreen());

		createGame(level);
	}

	private void createGame(final int level) {
		game = new Game();
		game.setLevel(level);
		game.addPlayer(DEFAULT_USERNAME, player);
		game.start();
	}

	/**
	 * The user pressed the pause button.
	 *
	 * @param view
	 */
	public void pressedPause(final View view) {
		game.pause(DEFAULT_USERNAME);
	}

	/**
	 * The user pressed the quit button.
	 *
	 * @param view
	 */
	public void pressedQuit(final View view) {
		// TODO
	}

	/**
	 * The user pressed the arrow up button.
	 *
	 * @param view
	 */
	public void pressedArrowUp(final View view) {
		controller.keyPressed('U');
	}

	/**
	 * The user pressed the arrow left button.
	 *
	 * @param view
	 */
	public void pressedArrowLeft(final View view) {
		controller.keyPressed('L');
	}

	/**
	 * The user pressed the arrow down button.
	 *
	 * @param view
	 */
	public void pressedArrowDown(final View view) {
		controller.keyPressed('D');
	}

	/**
	 * The user pressed the arrow right button.
	 *
	 * @param view
	 */
	public void pressedArrowRight(final View view) {
		controller.keyPressed('R');
	}

	/**
	 * The user pressed the bomb button.
	 *
	 * @param view
	 */
	public void pressedBomb(final View view) {
		controller.keyPressed('B');
	}
}
