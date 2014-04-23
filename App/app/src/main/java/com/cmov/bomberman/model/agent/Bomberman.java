package com.cmov.bomberman.model.agent;

import android.util.JsonWriter;
import com.cmov.bomberman.model.Position;
import com.cmov.bomberman.model.State;

import java.io.IOException;

public class Bomberman extends MovableAgent {

	private static final int MAX_MOVEMENT_STEP = 3;
	private static final int MAX_DIE_STEP = 6;

	private String ownerUsername = "";
	private int explosionRange;
	private int explosionTimeout;
	private int step;
	private String currentAction = "";
	private boolean isDestroyed;

	public Bomberman(Position pos, Algorithm ai, int range, int speed, String type, int timeout) {
		super(pos, ai, speed, type);
		explosionRange = range;
		explosionTimeout = timeout;
	}

	public String getOwnerUsername() { return ownerUsername;}

	public void setOwnerUsername(String username) { this.ownerUsername = username; }

	public boolean hasOwnerWithUsername(String username) {
		if (ownerUsername.equals(username)) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean isDestroyed() {
		return isDestroyed;
	}

	@Override
	public void play(State state) {
		String nextAction = getAlgorithm().getNextActionName();
		Move move = ActionToMove(nextAction);
		move(state, move);

		// when the action differs
		if (!currentAction.equals(nextAction)) {
			currentAction = nextAction;
			step = 0;
			if (nextAction.equals(BombermanActions.PUT_BOMB.toString())) {
				state.addAgent(
						new Bomb(this.getPosition(), explosionRange, Bomb.class.getSimpleName(), explosionTimeout));
			}
		} else if (currentAction.equals(AgentActions.DESTROY)) {
			if (step < MAX_DIE_STEP) {
				step++;
			} else if (step == MAX_DIE_STEP) {
				isDestroyed = true;
			}
		} else {
			// Cool effect, moves even when against a wall
			step = (step + 1) % MAX_MOVEMENT_STEP;
		}
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

			writer.name("currentAction").value(currentAction);
			writer.name("step").value(step);
			writer.endObject();
		}
		catch (IOException e) {

		}
	}

	private enum BombermanActions {
		PUT_BOMB;
	}
}
