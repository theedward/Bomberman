package com.cmov.bomberman.model;

import android.util.JsonWriter;

import java.io.IOException;

public class Bomberman extends MovableAgent {

	private static final int MAX_MOVEMENT_STEP = 3;
	private static final int MAX_DIE_STEP = 6;

    private String ownerUsername = "";
	private int explosionRange;
	private int step;
	private String currentAction;
	private boolean isDestroyed;

	public Bomberman(Position pos, Algorithm ai, int range, int speed, String type) {
		super(pos, ai, speed,type);
		explosionRange = range;
	}

    public String getOwnerUsername() { return ownerUsername;}

    public void setOwnerUsername(String username) { this.ownerUsername = username; }

    public boolean hasOwnerWithUsername(String username) {
        if (ownerUsername.equals(username)) {
            return true;
        } else return false;
    }

	private Move ActionToMove(String action) {

		if (action.equals(MovableAgentActions.MOVE_BOTTOM)) {
			return Move.DOWN;
		} else if (action.equals(MovableAgentActions.MOVE_TOP)) {
			return Move.UP;
		} else if (action.equals(MovableAgentActions.MOVE_LEFT)) {
			return Move.LEFT;
		} else if (action.equals(MovableAgentActions.MOVE_RIGHT)) {
			return Move.RIGHT;
		} else {
			return null;
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

        if (!currentAction.equals(nextAction)) {
            currentAction = nextAction;
            step = 0;
            if (nextAction.equals(BombermanActions.PUT_BOMB.toString())) {
                state.addAgent(new Bomb(this.getPosition(), explosionRange, "Bomb"));
            }
        } else if (currentAction.equals(AgentActions.DESTROY)) {
            if (step > 0 && step < MAX_DIE_STEP) {
                step = (step + 1) % 6;
            } else if (step == MAX_DIE_STEP) {
                isDestroyed = true;
                return;
            }
        } else if (step > 0 && step < MAX_MOVEMENT_STEP) {
            step = (step + 1) % 3;
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
