package com.cmov.bomberman.model;

import android.util.JsonWriter;

import java.io.IOException;

/**
 * Created by JoãoEduardo on 17/04/2014.
 */
public class Bomb extends Agent {
	private static final int BOMB_MAX_STEP = 3;
	private static final int EXPL_MAX_STEP = 4;

	private int bombRange;
	private int bombStep;
	private int explStep;

	//At the moment all bombs explode after 5 seconds, this can be passed to a constructor or changed
	private int timeout = 5;
	private boolean destroyed;

	public Bomb(final Position startingPos, int bombRange, String type) {
		super(startingPos, null, type);
		this.bombRange = bombRange;
	}

	@Override
	public void play(State state) {
		if (bombStep > 0 && bombStep < BOMB_MAX_STEP) {
			bombStep++;
		} else if (explStep == EXPL_MAX_STEP) {
			destroyed = true;
		} else if (bombStep == BOMB_MAX_STEP) {
			state.bombExplosion(bombRange, this);
			timeout = 0;
			explStep++;
		}
	}

	@Override
	public boolean isDestroyed() {
		return false;
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
}
