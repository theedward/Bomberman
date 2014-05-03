package com.cmov.bomberman.model;

/**
 * This is the class responsible for continuously running the game.
 */
public class GameThread extends Thread {
	private final String TAG = this.getClass().getSimpleName();

	/**
	 * Number of updates per second
	 */
	private final int numUpdates;
	private final GameImpl gameImpl;

	public GameThread(final GameImpl gameImpl) {
		this.gameImpl = gameImpl;
		this.numUpdates = this.gameImpl.getNumberUpdates();
	}

	/**
	 * Performs updates until the game has finished.
	 * Doesn't update when running is false.
	 * <p/>
	 * For better battery saving, the thread gets locked when running is false
	 * and only gets unlocked when running is true.
	 */
	public void run() {
		final int timeSleep = 1000 / numUpdates;
		while (!gameImpl.hasFinished()) {
			final long now = System.currentTimeMillis();

			gameImpl.update();

			try {
				final long dt = System.currentTimeMillis() - now;
				// suspend thread only when the time spent on game#update is smaller than the time it should
				// spend on each update (1000 / numUpdates).
				if (timeSleep > dt) {
					Thread.sleep(timeSleep - dt);
				}
			}
			catch (InterruptedException e) {
				return;
			}
		}
	}
}

