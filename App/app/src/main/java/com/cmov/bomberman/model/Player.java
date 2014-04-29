package com.cmov.bomberman.model;

import android.graphics.Canvas;
import android.util.JsonReader;

import com.cmov.bomberman.controller.GameActivity;
import com.cmov.bomberman.controller.GameView;
import com.cmov.bomberman.model.agent.Agent;
import com.cmov.bomberman.model.agent.Controllable;

import java.io.IOException;
import java.io.StringReader;

public class Player {
    private final String username;
    private final Screen screen;
    private final Controllable controller;
    private Agent agent;

    /**
     * This is needed to draw in the canvas in a synchronized manner.
     */
    private GameActivity gameActivity;
    private int score;

    public Player(String username, Controllable controller) {
        this.username = username;
        this.controller = controller;
        this.screen = new Screen();
        this.gameActivity = null;
        this.score = 0;
    }

    public Agent getAgent() {
        return agent;
    }

    public void setAgent(final Agent agent) {
        this.agent = agent;
    }

    public void setGameView(final GameActivity gameActivity) {
        this.gameActivity = gameActivity;
    }

    public Controllable getController() {
        return controller;
    }

    public String getUsername() {
        return username;
    }

    public int getScore() {
        return score;
    }

    public void addScore(final int amount) {
        this.score += amount;
    }

    public Screen getScreen() {
        return screen;
    }

    /**
     * This method is called when the game starts.
     * It adds all the fixed drawings to the screen. All the other drawings are added on Player#onUpdate.
     *
     * @param initialConfig the initial game configuration
     */
    void onGameStart(GameConfiguration initialConfig) {
        screen.setWallDrawings(initialConfig.getWallDrawings());
        screen.setDrawings(initialConfig.getMutableDrawings());
    }

    // This method will be called when the game finishes.

    /**
     * This method is called when the game finishes.
     * This can be useful to tell each player who is the winner. (if there's any winner)
     *
     * @param finalConfig the final game configuration
     */
    void onGameEnd(GameConfiguration finalConfig) {
        // TODO
    }

    /**
     * Updates creates or destroys the drawings from the json report sent by the game.
     * The fixed drawings are not included in the report.
     *
     * @param msg the json report.
     */
    private void parseMessage(String msg) {
        JsonReader rd = new JsonReader(new StringReader(msg));

        try {
            rd.beginArray();
            while (rd.hasNext()) {
                Position position = null;
                int step = 0;
                int lastStep = 0;
                String type = "";
                String currentAction = "";
                String lastAction = "";
                int range = 1;
                int drawingId = 0;
                boolean isDestroyed = false;

                // Parse object
                rd.beginObject();
                while (rd.hasNext()) {
                    String name = rd.nextName();
                    if (name != null) {
                        if (name.equals("type")) {
                            type = rd.nextString();
                        } else if (name.equals("currentAction")) {
                            currentAction = rd.nextString();
                        } else if (name.equals("lastAction")) {
                            lastAction = rd.nextString();
                        } else if (name.equals("step")) {
                            step = rd.nextInt();
                        } else if (name.equals("lastStep")) {
                            lastStep = rd.nextInt();
                        } else if (name.equals("position")) {
                            float x, y;
                            rd.beginArray();
                            x = (float) rd.nextDouble();
                            y = (float) rd.nextDouble();
                            position = new Position(x, y);
                            rd.endArray();
                        } else if (name.equals("range")) {
                            range = rd.nextInt();
                        } else if (name.equals("id")) {
                            drawingId = rd.nextInt();
                        } else if (name.equals("score")) {
                            this.score = rd.nextInt();
                        } else if (name.equals("isDestroyed")) {
                            isDestroyed = rd.nextBoolean();
                        }
                    }
                }
                rd.endObject();

                // updates object
                if (type != null) {
                    screen.updateDrawing(type, drawingId, position, currentAction, lastAction, step, lastStep, range,
                            isDestroyed);
                }
            }
            rd.endArray();
        } catch (IOException e) {
            System.out.println("Player#onUpdate: Error while parsing the message.");
        }
    }

    /**
     * This method will receive text containing the info needed to construct
     * new drawings on the client side.
     * It also draws everything on the canvas.
     *
     * @param msg the json report
     */
    void onUpdate(String msg) {
        parseMessage(msg);

        // update canvas
        final GameView gameView = gameActivity.getGameView();
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

        // update score
        gameActivity.updateScoreView(this.score);
    }
}
