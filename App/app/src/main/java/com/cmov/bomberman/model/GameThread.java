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

	/**
	 * If the game is running or it's paused
	 */
	private boolean running;

	public GameThread(final GameActivity activity, final Game game) {
		this.activity = activity;
		this.game = game;
		this.running = false;
		this.numUpdates = this.game.getNumberUpdates();
	}

	/**
	 * @return if the game is running.
	 */
	public boolean isRunning() {
		return running;
	}

	public void setRunning(final boolean running) {
		this.running = running;
		if (this.running) {
			synchronized (this) {
				this.notify();
			}
		}
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
			synchronized (this) {
				if (!running) {
					try {
						this.wait();
					}
					catch (InterruptedException e) {
						return;
					}
				}
			}

			game.update();

			try {
				Thread.sleep(1000 / numUpdates);
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		// game has finished
		activity.gameFinished();
	}
}
