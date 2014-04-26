package com.cmov.bomberman.model.agent;

import android.util.JsonWriter;
import com.cmov.bomberman.model.Position;
import com.cmov.bomberman.model.State;

import java.io.IOException;

public class Bomb extends Agent {
	private static final int BOMB_MAX_STEP = 3;
	private static final int EXPLOSION_MAX_STEP = 4;

	private final int range;

	/**
	 * Starts at -1 because it's always incremented before it's used.
	 */
	private int step;
	private int explosionStepIncr;
	private boolean explosion;
	private String currentAction;
	private boolean destroyed;
    private Bomberman owner;

	public Bomb(final Position startingPos, int range, int timeout, Bomberman owner) {
		super(startingPos, new BombAlgorithm(timeout));
		this.range = range;
		this.step = -1;
		this.currentAction = "";
		this.explosionStepIncr = 1;
        this.owner = owner;
	}

    public Bomberman getOwner(){
        return this.owner;
    }

	@Override
	public void play(State state, final long dt) {
		String nextAction = getAlgorithm().getNextActionName();

		if (!currentAction.equals(nextAction)) {
			// changed action, restart step
			currentAction = nextAction;
			step = -1;
			if (currentAction.equals(Actions.EXPLODE.toString())) {
				state.bombExplosion(range, this);
				explosion = true;
			}
		}

		if (explosion) {
			// during the explosion, the steps displayed should be [0 1 2 3 2 1 0]
			if (step < EXPLOSION_MAX_STEP) {
				step += explosionStepIncr;
			} else if (step == EXPLOSION_MAX_STEP) {
				explosionStepIncr = -1;
				step--;
			} else if (step == 0 && explosionStepIncr == -1) {
				destroyed = true;
			}
		} else {
			step = (step + 1) % BOMB_MAX_STEP;
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

			writer.name("step").value(this.step);

			writer.name("range").value(this.range);

			writer.name("isExplosion").value(this.currentAction);
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
