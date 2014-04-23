package com.cmov.bomberman.controller;

import android.app.Activity;
import android.os.Bundle;
import android.view.SurfaceView;
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
	private Player thisPlayer;
	private Controllable thisControllable;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);

		Bundle extras = getIntent().getExtras();
		Integer level = (Integer) extras.get("level");
		if (level == null) {
			System.out.println("Invalid level...");
			level = DEFAULT_LEVEL;
		}

		thisControllable = new Controllable(DEFAULT_KEYMAP);
		thisPlayer = new Player(DEFAULT_USERNAME, thisControllable);

		SurfaceView gameView = (SurfaceView) findViewById(R.id.canvas);
		//		thisPlayer.setMyScreen(new Screen(gameView.getHolder()));

		createGame(level);
	}

	private void createGame(final int level) {
		game = new Game();
		game.setLevel(level);

		game.addPlayer(DEFAULT_USERNAME, thisPlayer);

		game.start();
	}

}
