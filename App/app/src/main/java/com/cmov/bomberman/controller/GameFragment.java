package com.cmov.bomberman.controller;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.*;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.cmov.bomberman.R;
import com.cmov.bomberman.model.Game;
import com.cmov.bomberman.model.GameUtils;
import com.cmov.bomberman.model.Player;
import com.cmov.bomberman.model.Screen;
import com.cmov.bomberman.model.agent.Controllable;

import java.util.Map;

public class GameFragment extends Fragment implements SurfaceHolder.Callback {
	private final String TAG = this.getClass().getSimpleName();
	private boolean gamePaused;
	private boolean gameStarted;
	private Game game;
	private String username;
	private Player player;
	private Controllable playerController;
	private Handler mHandler = new Handler();
	private GameView gameView;
	private TextView scoreView;
	private TextView timeView;
	private TextView numPlayersView;

	public void setUsername(final String username) {
		this.username = username;
	}

	public void setGame(Game game) {
		this.game = game;
	}

	@Override
	public void onActivityCreated(final Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		Button btnPause = (Button) getActivity().findViewById(R.id.btnPause);
		btnPause.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View view) {
				pressedPause();
			}
		});

		Button btnQuit = (Button) getActivity().findViewById(R.id.btnQuit);
		btnQuit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View view) {
				quitGame();
			}
		});

		// Handle the touch events on the arrows and bomb buttons.
		// if the event's action is ACTION_DOWN, this is the next action
		// if the event's action is ACTION_UP, the next action is empty
		Button btnArrowUp = (Button) getActivity().findViewById(R.id.arrowUp);
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

		Button btnArrowLeft = (Button) getActivity().findViewById(R.id.arrowLeft);
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

		Button btnArrowDown = (Button) getActivity().findViewById(R.id.arrowDown);
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

		Button btnArrowRight = (Button) getActivity().findViewById(R.id.arrowRight);
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

		Button btnPutBomb = (Button) getActivity().findViewById(R.id.putBomb);
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

		this.gameView = (GameView) getActivity().findViewById(R.id.canvas);
		SurfaceHolder holder = this.gameView.getHolder();
		holder.addCallback(this);

		this.scoreView = (TextView) getActivity().findViewById(R.id.playerScore);
		this.timeView = (TextView) getActivity().findViewById(R.id.timeLeft);
		this.numPlayersView = (TextView) getActivity().findViewById(R.id.numPlayers);

		// This one is only changed once
		final TextView usernameView = (TextView) getActivity().findViewById(R.id.playerName);
		usernameView.setText(username);

		// Create player and its controller
		playerController = new Controllable();
		player = new Player(playerController, this);
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

				final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
							 final Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		return inflater.inflate(R.layout.fragment_game, container);
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
			final int imgSize = Math.min(gameView.getWidth() / game.getMapWidth(),
										 gameView.getHeight() / game.getMapHeight());
			// Set values on GameUtils
			GameUtils.IMG_CANVAS_WIDTH = imgSize;
			GameUtils.IMG_CANVAS_HEIGHT = imgSize;

			// Adjust GameView size
			final int width = imgSize * game.getMapWidth();
			final int height = imgSize * game.getMapHeight();

			ViewGroup.LayoutParams layoutParams = gameView.getLayoutParams();
			layoutParams.width = width;
			layoutParams.height = height;

			Log.i(TAG, "GameView w: " + width + " h:" + height);

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
			Log.e(TAG, "Game is not initialized in onStart");
		}
	}

	@Override
	public void surfaceDestroyed(final SurfaceHolder surfaceHolder) {
		// Nothing to do here
	}

	public GameView getGameView() {
		return gameView;
	}

	public void pressedPause() {
		if (gamePaused) {
			unpauseGame();
		} else {
			pauseGame();
		}
	}

	private void unpauseGame() {
		if (game != null) {
			game.unpause();
		} else {
			Log.e(TAG, "Can't unpause an uninitialized game");
		}

		gamePaused = false;
	}

	private void pauseGame() {
		if (game != null) {
			game.pause();
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

		Intent intent = new Intent(getActivity(), HomeActivity.class);
		intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}

	/**
	 * Called by the player when it's destroyed
	 */
	public void gameLost() {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(getActivity(), "You lost the game :(", Toast.LENGTH_SHORT).show();
			}
		});
	}

	public static class GameView extends SurfaceView {
		Screen screen;

		public GameView(final Context context, final AttributeSet attrs) {
			super(context, attrs);
		}

		public void setScreen(final Screen screen) {
			this.screen = screen;
		}

		@Override
		public void onDraw(Canvas canvas) {
			super.onDraw(canvas);

			if (screen != null) {
				screen.drawAll(canvas);
			}
		}
	}
}
