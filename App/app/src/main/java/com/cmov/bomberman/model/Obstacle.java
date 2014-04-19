package com.cmov.bomberman.model;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Obstacle extends Agent {
	private static final int SPRITE_LINE = 13;
	private static final int SPRITE_COLUMN = 1;
	private static final int NUM_IMAGES = 7;
	private static final int IMAGE_WIDTH = 10;
	private static final int IMAGE_HEIGHT = 10;
	private static final int MAX_STEP = 6;
	private static Bitmap[] sprite;

	/**
	 * The image to be displayed.
	 */
	private Bitmap obstacle;
	/**
	 * Step is the id of the image to be displayed.
	 */
	private int step;
	/**
	 * Represents if the obstacle is destroyed.
	 */
	private boolean destroyed;

	public Obstacle(final int id, final Position startingPos) {
		super(id, startingPos, null);

		// load sprites if it's the first obstacle
		if (sprite == null) {
			sprite = GameUtils.readCharacterSprite(SPRITE_LINE, SPRITE_COLUMN, NUM_IMAGES);
		}
	}

	@Override
	public void draw(final Canvas canvas) {
		canvas.drawBitmap(sprite[step], getPosition().getX(), getPosition().getY(), null);
	}

	// TODO: don't forget that the State class is not creating the new algorithm for obstacles

	@Override
	public void play(final State state) {
		if (step > 0 && step < MAX_STEP) {
			// increase step until it reaches MAX_STEP
			step++;
		} else if (step == MAX_STEP) {
			// when the MAX_STEP is reached, destroy the obstacle
			destroyed = true;
			return;
		} else {
			// check if the next action is DESTROY
			Algorithm ai = getAlgorithm();
			if (ai.getNextActionName().equals(Actions.DESTROY.toString())) {
				step++;
			}
		}
	}

	@Override
	public boolean isDestroyed() {
		return destroyed;
	}

	/**
	 * The possible actions of this agent.
	 */
	public enum Actions {
		DESTROY
	}
}
