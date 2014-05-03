package com.cmov.bomberman.controller;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import com.cmov.bomberman.R;
import com.cmov.bomberman.model.GameProxy;
import com.cmov.bomberman.model.OnGameStateListener;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class LoadingActivity extends Activity implements OnGameStateListener {
	private final String TAG = this.getClass().getSimpleName();
	private GameProxy gameProxy;
	private boolean mBound;
	/** Defines callbacks for service binding, passed to bindService() */
	private ServiceConnection mConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName className,
									   IBinder service) {
			// We've bound to GameService, cast the IBinder and get LocalService instance
			GameProxy.GameBinder binder = (GameProxy.GameBinder) service;
			gameProxy = binder.getService();
			gameProxy.setOnGameStateListener(LoadingActivity.this);

			// update list
			List<String> playerUsernames = new LinkedList<String>(gameProxy.getPlayerUsernames());
			playerListView.setAdapter(new ArrayAdapter<String>(LoadingActivity.this,
															   android.R.layout.simple_list_item_1,
															   playerUsernames));
			mBound = true;
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			// Nothing to do here
			gameProxy = null;
			mBound = false;
		}
	};
	private String username;
	private boolean isOwner;

	private ListView playerListView;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

		// get data from previous activity
		final Bundle extras = this.getIntent().getExtras();
		if (extras != null) {
			username = extras.getString("username");
			isOwner = extras.getBoolean("isOwner");
		}

		playerListView = (ListView) findViewById(R.id.playerList);

		// depending on if it's owner, show message or show start button
		if (isOwner) {
			TextView txtWaitingGameStart = (TextView) findViewById(R.id.txtWaitingGameStart);
			txtWaitingGameStart.setVisibility(View.INVISIBLE);
		} else {
			Button btnStart = (Button) findViewById(R.id.btnStart);
			btnStart.setVisibility(View.INVISIBLE);
		}
    }

	@Override
	protected void onStart() {
		super.onStart();
		Log.i(TAG, "Binding to service");

		// Bind to GameService
		Intent intent = new Intent(this, GameProxy.class);
		bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
	}

	@Override
	protected void onStop() {
		super.onStop();

		// Unbind from the service
		if (mBound) {
			unbindService(mConnection);
			mBound = false;
		}
	}

	@Override
	public void onGameStart() {
		// Game has started. Jump to the game activity
		Intent intent = new Intent(LoadingActivity.this, GameActivity.class);
		intent.putExtra("username", username);
		startActivity(intent);
	}

	@Override
	public void onGameUpdate(Collection<String> playerUsernames) {
		List<String> usernames = new LinkedList<String>(playerUsernames);
		playerListView.setAdapter(new ArrayAdapter<String>(LoadingActivity.this,
														   android.R.layout.simple_list_item_1,
														   usernames));
	}

	@Override
	public void onGameEnd() {
		// Nothing to do here
	}

	/**
	 * Start the game.
	 * @param view the button view that was pressed
	 */
	protected void start(final View view) {
		gameProxy.start();
	}
}
