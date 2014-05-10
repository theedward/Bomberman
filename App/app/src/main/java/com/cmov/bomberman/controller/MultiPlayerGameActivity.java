package com.cmov.bomberman.controller;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.cmov.bomberman.R;
import com.cmov.bomberman.model.*;
import com.cmov.bomberman.model.agent.Controllable;
import com.cmov.bomberman.model.net.GameClient;
import com.cmov.bomberman.model.net.GameServer;

import java.util.Map;

public class MultiPlayerGameActivity extends Activity implements SurfaceHolder.Callback, PlayerActionListener {
	private final String TAG = this.getClass().getSimpleName();

	private boolean gamePaused;
	private boolean gameStarted;
	private Game game;
	private String username;
	private PlayerImpl player;
	private Controllable playerController;
	private Handler mHandler = new Handler();
	private GameView gameView;
	private TextView scoreView;
	private TextView timeView;
	private TextView numPlayersView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);

		GameUtils.createInstance(getApplicationContext());

		int level = 1;
		boolean isServer = false;
		String hostname = "";
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			isServer = extras.getBoolean("isServer");
			this.username = extras.getString("username");

			if (isServer) {
				level = extras.getInt("level");
			} else {
				hostname = extras.getString("hostname");
			}
		}

		// Handle the touch events on the arrows and bomb buttons.
		// if the event's action is ACTION_DOWN, this is the next action
		// if the event's action is ACTION_UP, the next action is empty
		Button btnArrowUp = (Button) findViewById(R.id.arrowUp);
		btnArrowUp.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent motionEvent) {
				if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
					playerController.keyPressed('U');
					return true;
				} else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
					playerController.keyPressed(' ');
					return true;
				} else {
					return false;
				}
			}
		});

		Button btnArrowLeft = (Button) findViewById(R.id.arrowLeft);
		btnArrowLeft.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent motionEvent) {
				if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
					playerController.keyPressed('L');
					return true;
				} else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
					playerController.keyPressed(' ');
					return true;
				} else {
					return false;
				}
			}
		});

		Button btnArrowDown = (Button) findViewById(R.id.arrowDown);
		btnArrowDown.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent motionEvent) {
				if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
					playerController.keyPressed('D');
					return true;
				} else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
					playerController.keyPressed(' ');
					return true;
				} else {
					return false;
				}
			}
		});

		Button btnArrowRight = (Button) findViewById(R.id.arrowRight);
		btnArrowRight.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent motionEvent) {
				if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
					playerController.keyPressed('R');
					return true;
				} else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
					playerController.keyPressed(' ');
					return true;
				} else {
					return false;
				}
			}
		});

		Button btnPutBomb = (Button) findViewById(R.id.putBomb);
		btnPutBomb.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent motionEvent) {
				if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
					playerController.keyPressed('B');
					return true;
				} else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
					playerController.keyPressed(' ');
					return true;
				} else {
					return false;
				}
			}
		});

		this.gameView = (GameView) findViewById(R.id.canvas);
		SurfaceHolder holder = this.gameView.getHolder();
		holder.addCallback(this);

		this.scoreView = (TextView) findViewById(R.id.playerScore);
		this.timeView = (TextView) findViewById(R.id.timeLeft);
		this.numPlayersView = (TextView) findViewById(R.id.numPlayers);

		// This one is only changed once
		final TextView usernameView = (TextView) findViewById(R.id.playerName);
		usernameView.setText(username);

		// Create player and its controller
		this.playerController = new Controllable();

		final Screen screen = new Screen();
		this.gameView.setScreen(screen);
		this.player = new PlayerImpl(playerController, screen);
		this.player.setPlayerActionListener(this);

		if (isServer) {
			this.game = new GameServer(level);
			game.join(username, player);
		} else {
			this.game = new GameClient(hostname);
		}
	}

	@Override
	public void onBackPressed() {
		pressedPause(null);
	}

	@Override
	public void onResume() {
		super.onResume();
		unpauseGame();
	}

	@Override
	public void onPause() {
		super.onPause();
		pauseGame();
	}

	@Override
	public void surfaceCreated(final SurfaceHolder surfaceHolder) {
		// Nothing to do here
	}

	@Override
	public void surfaceChanged(final SurfaceHolder surfaceHolder, final int i, final int i2, final int i3) {
		if (game != null) {
			if (!gameStarted) {
				// Before starting the game, let the player join it
				game.join(username, player);

				// Start game
				new Thread(new Runnable() {
					@Override
					public void run() {
						game.start();
					}
				}).start();

				gamePaused = false;
				gameStarted = true;
			}
		} else {
			Log.e(TAG, "Game is not initialized in surfaceChanged");
		}
	}

	@Override
	public void surfaceDestroyed(final SurfaceHolder surfaceHolder) {
		// Nothing to do here
	}

	public void pressedPause(final View view) {
		if (gamePaused) {
			unpauseGame();
		} else {
			pauseGame();
		}
	}

	public void pressedQuit(final View view) {
		quitGame();
	}

	private void unpauseGame() {
		if (game != null) {
			game.unpause(username);
		} else {
			Log.e(TAG, "Can't unpause an uninitialized game");
		}

		gamePaused = false;
	}

	private void pauseGame() {
		if (game != null) {
			game.pause(username);
		} else {
			Log.e(TAG, "Can't pause an uninitialized game");
		}

		gamePaused = true;
	}

	/**
	 * Destroys the game thread and jumps to the home activity, cleaning up the activity stack.
	 */
	private void quitGame() {
		game.quit(username);

		Intent intent = new Intent(this, HomeActivity.class);
		intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}

	@Override
	public void onScoreChange(final int score) {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				scoreView.setText("Score: " + score);
			}
		});
	}

	@Override
	public void onFinalScores(final Map<String, Integer> scores) {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				final StringBuilder sb = new StringBuilder();
				for (Map.Entry<String, Integer> entry : scores.entrySet()) {
					sb.append(entry.getKey());
					sb.append(": ");
					sb.append(entry.getValue());
					sb.append(" points\n");
				}

				final AlertDialog.Builder builder = new AlertDialog.Builder(MultiPlayerGameActivity.this);
				builder.setMessage(sb.toString()).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						quitGame();
					}
				});
				final Dialog dialog = builder.create();
				dialog.show();
			}
		});
	}

	@Override
	public void onTimeChange(final int time) {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				timeView.setText("Time Left: " + time);
			}
		});
	}

	@Override
	public void onNumPlayersChange(final int numPlayers) {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				numPlayersView.setText("Num. Players: " + numPlayers);
			}
		});
	}

	@Override
	public void onMapSizeChange(final int width, final int height) {
		final int imgSize = Math.min(gameView.getWidth() / width, gameView.getHeight() / height);
		// Set values on GameUtils
		GameUtils.getInstance().setImageSizeOnCanvas(imgSize, imgSize);

		// Adjust GameView size
		final int canvasWidth = imgSize * width;
		final int canvasHeight = imgSize * height;

		ViewGroup.LayoutParams layoutParams = gameView.getLayoutParams();
		layoutParams.width = canvasWidth;
		layoutParams.height = canvasHeight;
	}

	@Override
	public void onDeath() {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(MultiPlayerGameActivity.this, "You lost the game :(", Toast.LENGTH_SHORT).show();
			}
		});
	}

	@Override
	public void draw() {
		Canvas canvas = null;
		try {
			if (gameView.getHolder() != null) {
				canvas = gameView.getHolder().lockCanvas();
				synchronized (gameView.getHolder()) {
					if (canvas != null) {
						gameView.onDraw(canvas);
					}
				}
			}
		} finally {
			if (canvas != null) {
				gameView.getHolder().unlockCanvasAndPost(canvas);
			}
		}
	}
}

