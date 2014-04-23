package com.cmov.bomberman.model.agent;

import android.util.JsonWriter;
import com.cmov.bomberman.model.Position;
import com.cmov.bomberman.model.State;

import java.io.IOException;

public class Bomb extends Agent {
	private static final int BOMB_MAX_STEP = 3;
	private static final int EXPL_MAX_STEP = 4;

	private int bombRange;
	private int step;
	private int incr;
	private boolean explosion;
	private String currentAction = "";
	private boolean destroyed;

	public Bomb(final Position startingPos, int bombRange, String type, int timeout) {
		super(startingPos, new BombAlgorithm(System.currentTimeMillis(), timeout), type);
		this.bombRange = bombRange;
	}

	@Override
	public void play(State state) {
		String nextAction = getAlgorithm().getNextActionName();

		if (!currentAction.equals(nextAction)) {
			currentAction = nextAction;
			step = 0;
			incr = 1;
			if (currentAction.equals(BombActions.EXPLODE.toString())) {
				state.bombExplosion(bombRange, this);
				explosion = true;
			}
		} else if (explosion) {
			if (step < EXPL_MAX_STEP) {
				step += incr;
			} else if (step == EXPL_MAX_STEP) {
				incr = -1;
			} else if (step == 0 && incr == -1) {
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

			writer.name("currentAction").value("");
			writer.name("step").value(0);
			writer.endObject();
		}
		catch (IOException e) {

		}
	}

	private enum BombActions {
		EXPLODE;
	}

}
