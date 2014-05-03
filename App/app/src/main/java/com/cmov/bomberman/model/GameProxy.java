package com.cmov.bomberman.model;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class GameProxy extends Service implements Game {
	private final String TAG = this.getClass().getSimpleName();
	private final IBinder mBinder = new GameBinder();

	private GameImpl game;
	private GameThread gameThread;
	private boolean isMultiplayer;
	private int level;

	private void init() {
		game = new GameImpl(level);
		gameThread = new GameThread(game);

		Log.i(TAG, "Created the game");
	}

	@Override
	public int onStartCommand(final Intent intent, final int flags, final int startId) {
		final Bundle extras = intent.getExtras();
		if (extras != null) {
			this.level = extras.getInt("level");
			this.isMultiplayer = extras.getBoolean("isMultiplayer");
		}

		GameUtils.CONTEXT = this;
		init();

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

	@Override
	public void registerPlayer(final String username, final Player player) {
		// TODO
	}

	@Override
	public void pause(final String username) {
		// TODO
	}

	@Override
	public void unpause(final String username) {
	   // TODO
	}

	@Override
	public void quit(final String username) {
	   // TODO
	}

	@Override
	public void join(final String username, final Player player) {
		// TODO
	}

	@Override
	public void begin() {
		 // TODO
	}

	@Override
	public void end() {
		// TODO
	}

	@Override
	public void update() {
		// TODO
	}

	@Override
	public int getMapWidth() {
		// TODO
		return 0;
	}

	@Override
	public int getMapHeight() {
		// TODO
		return 0;
	}

	public class GameBinder extends Binder {
		public GameProxy getService() {
			// Return this instance of GameService so clients can call public methods
			return GameProxy.this;
		}
	}
}
