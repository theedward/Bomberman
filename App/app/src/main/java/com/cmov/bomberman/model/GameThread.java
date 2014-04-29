package com.cmov.bomberman.model;

import com.cmov.bomberman.controller.GameActivity;

/**
 * This is the class responsible for continuously running the game.
 */
public class GameThread extends Thread {
    /**
     * Number of updates per second
     */
    private final int numUpdates;
    private final Game game;
    private final GameActivity activity;

    public GameThread(final GameActivity activity, final Game game) {
        this.activity = activity;
        this.game = game;
        this.numUpdates = this.game.getNumberUpdates();
    }

    /**
     * Performs updates until the game has finished.
     * Doesn't update when running is false.
     * <p/>
     * For better battery saving, the thread gets locked when running is false
     * and only gets unlocked when running is true.
     */
    public void run() {
        while (!game.hasFinished()) {
            // Handle interruption
            if (this.isInterrupted()) {
                return;
            }

            game.update();

            try {
                Thread.sleep(1000 / numUpdates);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // game has finished
        activity.gameFinished();
    }
}
