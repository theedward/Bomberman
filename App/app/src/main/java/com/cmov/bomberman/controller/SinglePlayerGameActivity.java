package com.cmov.bomberman.controller;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import com.cmov.bomberman.R;
import com.cmov.bomberman.model.Game;
import com.cmov.bomberman.model.GameImpl;
import com.cmov.bomberman.model.GameUtils;

public class SinglePlayerGameActivity extends Activity {
	private final String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

		// Get the level
		int level = 1;
		String username = "";
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			level = extras.getInt("level");
			username = extras.getString("username");
		} else {
			Log.e(TAG, "Didn't receive the level in the bundle");
		}

		GameUtils.CONTEXT = this;
		Game game = new GameImpl(level);

		// Get the fragment and pass it the game and the username
		GameFragment fragment = (GameFragment) getFragmentManager().findFragmentById(R.id.gameFragment);
		if (fragment != null) {
			fragment.setUsername(username);
			fragment.setGame(game);
		} else {
			Log.e(TAG, "Can't find the game fragment");
		}
    }

	@Override
	public void onBackPressed() {
		super.onBackPressed();

		// if activeFragment == GameFragment
		GameFragment fragment = (GameFragment) getFragmentManager().findFragmentById(R.id.gameFragment);
		fragment.pressedPause();
	}
}
