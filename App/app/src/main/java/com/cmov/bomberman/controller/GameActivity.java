package com.cmov.bomberman.controller;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.cmov.bomberman.R;
import com.cmov.bomberman.model.Game;
import com.cmov.bomberman.model.GameThread;
import com.cmov.bomberman.model.GameUtils;
import com.cmov.bomberman.model.Player;
import com.cmov.bomberman.model.agent.Controllable;

import java.util.Map;

/**
 * TODO support Mode SINGLE_PLAYER and MULTI_PLAYER
 */
public class GameActivity extends Activity {
    private static final String DEFAULT_USERNAME = "Bomberman";

    private final Handler mHandler = new Handler();

    private Game game;
    private GameThread gameThread;
    private Controllable playerController;
	private String playerUsername;

    private int level;
	private boolean isMultiplayer;
    private boolean onPause = false;

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
			level = (Integer) extras.get("level");
			isMultiplayer = (Boolean) extras.get("isMultiplayer");
		}

		// TODO Create a player (currently SINGLE_PLAYER)
		playerUsername = DEFAULT_USERNAME;
		playerController = new Controllable();
		Player player = new Player(playerController);

		gameView = (GameView) findViewById(R.id.canvas);
		player.setGameView(this);
		gameView.setScreen(player.getScreen());

        game = new Game(level);
        game.registerPlayer(DEFAULT_USERNAME, player);
        gameThread = new GameThread(game);

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

    public GameView getGameView() {
        return gameView;
    }

    private void pauseGame() {
        if (onPause) {
            game.unpause(DEFAULT_USERNAME);
        } else {
            game.pause(DEFAULT_USERNAME);
        }
        onPause = !onPause;
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
        gameThread.interrupt();

        Intent intent = new Intent(GameActivity.this, HomeActivity.class);
        intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
    
    @Override
    public void onBackPressed() {
        pauseGame();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        // Start thread if it's the first time
        if (hasFocus && this.gameThread.getState() == Thread.State.NEW) {
            // Initialize the GameUtils image parameters
            // This is used in the Game#populateGame
            final int imgSize = Math.min(this.gameView.getWidth() / this.game.getMapWidth(),
									  this.gameView.getHeight() / this.game.getMapHeight());
			// Set values on GameUtils
			GameUtils.IMG_CANVAS_WIDTH = imgSize;
			GameUtils.IMG_CANVAS_HEIGHT = imgSize;

			// Adjust GameView size
			final int width = imgSize * this.game.getMapWidth();
			final int height = imgSize * this.game.getMapHeight();
			RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width, height);
			layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
			layoutParams.addRule(RelativeLayout.BELOW, R.id.dataLayout);
			this.gameView.setLayoutParams(layoutParams);

            this.game.populateGame();
            this.gameThread.start();
        }
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
