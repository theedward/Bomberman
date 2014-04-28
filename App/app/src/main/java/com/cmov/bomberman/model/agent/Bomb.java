package com.cmov.bomberman.model.agent;

import android.util.JsonWriter;
import com.cmov.bomberman.model.Position;
import com.cmov.bomberman.model.State;

import java.io.IOException;

public class Bomb extends Agent {
	private static final int BOMB_MAX_STEP = 3;
	private static final int EXPLOSION_MAX_STEP = 4;

	private final int range;

	private int explosionStepIncr;
	private boolean explosion;
	private boolean destroyed;
	private Bomberman owner;

	public Bomb(final Position startingPos, int id, int range, int timeout, Bomberman owner) {
		super(startingPos, new BombAlgorithm(timeout), id);
		this.range = range;
		this.explosionStepIncr = 1;
		this.owner = owner;
		setStep(0);
	}

	public Bomberman getOwner() {
		return this.owner;
	}

	@Override
	public void play(State state, final float dt) {
		String nextAction = getAlgorithm().getNextActionName();

		if (!this.getCurrentAction().equals(nextAction)) {
			// changed action, restart step
			this.setLastAction(this.getCurrentAction());
			this.setCurrentAction(nextAction);
			this.setLastStep(this.getStep());
			this.setStep(-1);
			if (this.getCurrentAction().equals(Actions.EXPLODE.toString())) {
				state.bombExplosion(range, this);
				explosion = true;
			}
		}

		if (explosion) {
			// during the explosion, the steps displayed should be [0 1 2 3 2 1 0]
			if (this.getStep() < EXPLOSION_MAX_STEP) {
				this.setStep(this.getStep() + explosionStepIncr);
			}

			if (this.getStep() == EXPLOSION_MAX_STEP) {
				explosionStepIncr = -1;
				this.setStep(this.getStep() - 1);
			} else if (this.getStep() == 0 && explosionStepIncr == -1) {
				destroyed = true;
			}
		} else {
			this.setStep((this.getStep() + 1) % BOMB_MAX_STEP);
		}
	}

	@Override
	public boolean isDestroyed() {
		return destroyed;
	}

	@Override
	public void toJson(JsonWriter writer) {
		try {
			writer.beginObject();
			writer.name("type").value(getType());

			writer.name("position");
			writer.beginArray();
			writer.value(getPosition().getX() - 0.5f);
			writer.value(getPosition().getY() - 0.5f);
			writer.endArray();

			writer.name("step").value(this.getStep());
			writer.name("lastStep").value(this.getLastStep());

			writer.name("range").value(this.range);

			writer.name("currentAction").value(this.getCurrentAction());
			writer.name("lastAction").value(this.getLastAction());
			writer.name("id").value(this.getId());
			writer.name("isDestroyed").value(isDestroyed());
			writer.endObject();
		}
		catch (IOException e) {
			System.out.println("Bomb#toJson: Error while serializing to json.");
		}
	}

	public enum Actions {
		EXPLODE
	}

}
