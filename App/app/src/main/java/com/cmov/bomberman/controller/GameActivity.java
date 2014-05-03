package com.cmov.bomberman.controller;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.*;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.cmov.bomberman.R;
import com.cmov.bomberman.model.Game;
import com.cmov.bomberman.model.GameProxy;
import com.cmov.bomberman.model.GameUtils;
import com.cmov.bomberman.model.Player;
import com.cmov.bomberman.model.agent.Controllable;

import java.util.Map;

public class GameActivity extends Activity {
    private final Handler mHandler = new Handler();
	private boolean mBound = false;
	private Game game;

	/** Defines callbacks for service binding, passed to bindService() */
	private ServiceConnection mConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName className,
									   IBinder service) {
			// We've bound to GameService, cast the IBinder and get LocalService instance
			GameProxy.GameBinder binder = (GameProxy.GameBinder) service;
			GameProxy mService = binder.getService();
			game = (Game) mService;
			mBound = true;
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			mBound = false;
		}
	};
	private String playerUsername;
	private Controllable playerController;
    private boolean isPaused = false;

	/**
	 * Only initialize some image parameters once
	 */
	private boolean once = false;

    private GameView gameView;
    private TextView scoreView;
	private TextView timeView;
	private TextView numPlayersView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // Initialize the GameUtils context parameter needed in almost every method.
        GameUtils.CONTEXT = this;

		// Get the level
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			playerUsername = (String) extras.get("username");
		}

		playerController = new Controllable();
		Player player = new Player(playerController);
		player.setGameActivity(this);
		game.registerPlayer(playerUsername, player);

		gameView = (GameView) findViewById(R.id.canvas);
		gameView.setScreen(player.getScreen());

		initViews();
    }

	private void initViews() {
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

		this.scoreView = (TextView) findViewById(R.id.playerScore);
		this.timeView = (TextView) findViewById(R.id.timeLeft);
		this.numPlayersView = (TextView) findViewById(R.id.numPlayers);

		TextView usernameView = (TextView) findViewById(R.id.playerName);
		usernameView.setText(playerUsername);
	}

	@Override
	protected void onStart() {
		super.onStart();

		// Bind to GameService
		Intent intent = new Intent(this, GameProxy.class);
		bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
	}

	@Override
	public void onTrimMemory(final int level) {
		super.onTrimMemory(level);
		// TODO
	}

	@Override
	protected void onResume() {
		super.onResume();
		// TODO
	}

	@Override
	protected void onPause() {
		super.onPause();
		// TODO
	}

	@Override
	protected void onStop() {
		super.onStop();
		// TODO

		// Unbind from the service
		if (mBound) {
			unbindService(mConnection);
			mBound = false;
		}
	}

	@Override
	protected void onSaveInstanceState(final Bundle outState) {
		super.onSaveInstanceState(outState);
		// TODO
	}

	@Override
	public void onBackPressed() {
		pauseGame();
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);

		// Define the size of the canvas
		if (hasFocus && once) {
			once = true;

			// Initialize the GameUtils image parameters
			final int imgSize = Math.min(this.gameView.getWidth() / this.game.getMapWidth(),
										 this.gameView.getHeight() / this.game.getMapHeight());
			// Set values on GameUtils
			GameUtils.IMG_CANVAS_WIDTH = imgSize;
			GameUtils.IMG_CANVAS_HEIGHT = imgSize;

			// Adjust GameView size
			final int width = imgSize * this.game.getMapWidth();
			final int height = imgSize * this.game.getMapHeight();
			RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width, height);
			layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
			this.gameView.setLayoutParams(layoutParams);
		}
	}

	public GameView getGameView() {
        return gameView;
    }

    private void pauseGame() {
        if (isPaused) {
            game.unpause(playerUsername);
        } else {
            game.pause(playerUsername);
        }
        isPaused = !isPaused;
    }

    /**
     * The user pressed the pause button.
     * Pauses the game if the game is running or continues the game if the game is paused.
     *
     * @param view the pause button
     */
    public void pressedPause(final View view) {
        pauseGame();
    }

    /**
     * The user pressed the quit button.
     * Kills the application
     *
     * @param view the quit button.
     */
    public void pressedQuit(final View view) {
        quit();
    }

	/**
	 * Destroys the game thread and jumps to the home activity, cleaning up the activity stack.
	 */
    private void quit() {
        game.quit(playerUsername);

        Intent intent = new Intent(GameActivity.this, HomeActivity.class);
        intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void updateScoreView(final int score) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                scoreView.setText("Score: " + score);
            }
        });
    }

    public void updateTimeView(final int timeLeft) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                timeView.setText("Time Left: " + timeLeft);
            }
        });
    }

	public void updateNumPlayersView(final int numPlayers) {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				numPlayersView.setText("Num. Players: " + numPlayers);
			}
		});
	}

    public void scoreDialog(final Map<String, Integer> scores) {
		final Activity currentActivity = this;
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

				final AlertDialog.Builder builder = new AlertDialog.Builder(currentActivity);
				builder.setMessage(sb.toString())
					   .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
						   public void onClick(DialogInterface dialog, int id) {
							   quit();
						   }
					   });
				final Dialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    public void gameLost() {
        final Activity currentActivity = this;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(currentActivity, "You lost the game :(", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
