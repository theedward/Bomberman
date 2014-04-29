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
import android.widget.TextView;
import android.widget.Toast;

import com.cmov.bomberman.R;
import com.cmov.bomberman.model.Game;
import com.cmov.bomberman.model.GameThread;
import com.cmov.bomberman.model.GameUtils;
import com.cmov.bomberman.model.Player;
import com.cmov.bomberman.model.agent.Controllable;

import java.util.TreeMap;

/**
 * TODO support Mode SINGLE_PLAYER and MULTI_PLAYER
 */
public class GameActivity extends Activity {
    private static final String DEFAULT_USERNAME = "Bomberman";

    private final Handler mHandler = new Handler();

    private Game game;
    private GameThread gameThread;
    private Controllable playerController;
    private int level;
    private boolean onPause = false;

    private GameView gameView;
    private TextView scoreView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // Initialize the GameUtils context parameter needed in almost every method.
        GameUtils.CONTEXT = this;

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

        // Get the level
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            level = (Integer) extras.get("level");
        }

        // TODO Create a player (currently SINGLE_PLAYER)
        playerController = new Controllable();
        Player player = new Player(DEFAULT_USERNAME, playerController);

        this.gameView = (GameView) findViewById(R.id.canvas);
        this.scoreView = (TextView) findViewById(R.id.playerScore);
        player.setGameView(this);
        this.gameView.setScreen(player.getScreen());

        this.game = new Game(level);
        this.game.addPlayer(DEFAULT_USERNAME, player);

        this.gameThread = new GameThread(this, game);
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

    /**
     * The user pressed the arrow up button.
     *
     * @param view
     */
    public void pressedArrowUp(final View view) {
        playerController.keyPressed('U');
    }

    /**
     * The user pressed the arrow left button.
     *
     * @param view
     */
    public void pressedArrowLeft(final View view) {
        playerController.keyPressed('L');
    }

    /**
     * The user pressed the arrow down button.
     *
     * @param view
     */
    public void pressedArrowDown(final View view) {
        playerController.keyPressed('D');
    }

    /**
     * The user pressed the arrow right button.
     *
     * @param view
     */
    public void pressedArrowRight(final View view) {
        playerController.keyPressed('R');
    }

    /**
     * The user pressed the bomb button.
     *
     * @param view
     */
    public void pressedBomb(final View view) {
        playerController.keyPressed('B');
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        // Start thread if it's the first time
        if (hasFocus && this.gameThread.getState() == Thread.State.NEW) {
            // Initialize the GameUtils image parameters
            // This is used in the Game#populateGame
            final int size = Math.min(Math.round(this.gameView.getWidth() / this.game.getMapWidth()),
                    Math.round(this.gameView.getHeight() / this.game.getMapHeight()));
            GameUtils.IMG_CANVAS_WIDTH = size;
            GameUtils.IMG_CANVAS_HEIGHT = size;

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

    public void callDialog(final TreeMap<String, Integer> scores){
        System.out.println("Calling Dialog");
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Dialog dialog = createDialog(scores);
                dialog.show();
            }
        });
    }

    public void gameLost() {
        final Activity currentActivity = this;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(currentActivity, "You lost the game!", Toast.LENGTH_SHORT).show();
            }
        });
        return;
    }

    public Dialog createDialog(TreeMap<String, Integer> scores){
        System.out.println("Creating Dialog");
        String output = "Player: " + scores.firstEntry().getKey() + " had the score: " + scores.firstEntry().getValue().toString();
        System.out.println(output);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(output)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        quit();
                    }
                });
        return builder.create();
    }
}
