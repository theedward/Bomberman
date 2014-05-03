package com.cmov.bomberman.model;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class GameService extends Service {
	private final String TAG = this.getClass().getSimpleName();
	private final IBinder mBinder = new GameBinder();

	private Game game;
	private GameThread gameThread;
	private boolean isMultiplayer;
	private int level;

	private void init(final int level) {
		game = new Game(level);
		gameThread = new GameThread(game);
		gameThread.start();
		Log.i(TAG, "Created the game");
	}

	@Override
	public int onStartCommand(final Intent intent, final int flags, final int startId) {
		final Bundle extras = intent.getExtras();
		if (extras != null) {
			this.level = extras.getInt("level");
			this.isMultiplayer = extras.getBoolean("isMultiplayer");

			init(level);
		}
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
    public IBinder onBind(Intent intent) {

        return mBinder;
    }

	@Override
	public void onDestroy() {
		super.onDestroy();

		gameThread.interrupt();
		game = null;
		gameThread = null;
	}

	public Game getGame() {
		return game;
	}

	public class GameBinder extends Binder {
		public GameService getService() {
			// Return this instance of GameService so clients can call public methods
			return GameService.this;
		}
	}
}
