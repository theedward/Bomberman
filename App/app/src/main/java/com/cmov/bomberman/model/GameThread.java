package com.cmov.bomberman.model;

public class GameThread extends Thread {
	/**
	 * Number of updates per second
	 */
	private final int numUpdates;
	private Game game;
	private boolean running;

	public GameThread(final Game game) {
		this.game = game;
		this.running = false;
		this.numUpdates = this.game.getNumberUpdates();
	}

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

	public void run() {
		while (!game.hasFinished()) {
			synchronized (this) {
				if (!running) {
					try {
						this.wait();
					}
					catch (InterruptedException e) {
						// Empty on purpose
					}
				}
			}

			game.update();

			try {
				Thread.sleep(1000 / numUpdates);
			}
			catch (InterruptedException e) {
				// No problem, just continue
			}
		}
	}
}
