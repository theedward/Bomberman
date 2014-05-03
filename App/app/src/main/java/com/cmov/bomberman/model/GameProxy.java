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
		Log.i(TAG, "onStartCommand");
		Bundle extras = intent.getExtras();
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
	public void pause(final String username) {
		if (isMultiplayer) {
			// TODO
		} else {
			game.pause(username);
		}
	}

	@Override
	public void unpause(final String username) {
	   if (isMultiplayer) {
		   // TODO
	   } else {
		   game.pause(username);
	   }
	}

	@Override
	public void quit(final String username) {
	   if (isMultiplayer) {
		   // TODO
	   } else {
		   game.quit(username);
	   }
	}

	@Override
	public void join(final String username, Player player) {
		// TODO
		if (isMultiplayer) {
			// TODO create network player or something
		} else {
			game.join(username, player);
		}
	}

	@Override
	public int getMapWidth() {
		if (isMultiplayer) {
			// TODO
			return 0;
		} else {
			return game.getMapWidth();
		}
	}

	@Override
	public int getMapHeight() {
		if (isMultiplayer) {
			// TODO
			return 0;
		} else {
			return game.getMapHeight();
		}
	}

	@Override
	public void start() {
		if (isMultiplayer) {
			// TODO
		} else {
			gameThread.start();
		}
	}

	public class GameBinder extends Binder {
		public GameProxy getService() {
			// Return this instance of GameService so clients can call public methods
			return GameProxy.this;
		}
	}
}
