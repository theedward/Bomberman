package com.cmov.bomberman.model.agent;

import android.util.JsonWriter;
import com.cmov.bomberman.model.Position;
import com.cmov.bomberman.model.State;

import java.io.IOException;

public class Bomberman extends MovableAgent {
	private static final int MAX_MOVEMENT_STEP = 3;
	private static final int MAX_DIE_STEP = 6;

	private final int timeBetweenBombs;
	private final int explosionRange;
	private final int explosionTimeout;
	/**
	 * This is initialized with time between bombs because a player
	 * is allowed to put a bomb right in the beginning.
	 */
	private long timeSinceLastBomb;
	private int step;
	private String currentAction;
	private boolean destroyed;

	/**
	 * @param pos the agent position
	 * @param ai the agent algorithm
	 * @param speed the agent speed
	 * @param timeBetweenBombs time between bombs in milliseconds.
	 * @param range the bomb range
	 * @param timeout the bomb timeout
	 */
	public Bomberman(Position pos, Algorithm ai, int speed, int timeBetweenBombs, int range, int timeout) {
		super(pos, ai, speed);
		this.timeBetweenBombs = timeBetweenBombs * 1000;
		this.timeSinceLastBomb = this.timeBetweenBombs;
		this.explosionRange = range;
		this.explosionTimeout = timeout;
		this.step = 0;
		this.currentAction = "";
		this.destroyed = false;
	}

	@Override
	public boolean isDestroyed() {
		return destroyed;
	}

	@Override
	public void play(State state, final long dt) {
		String nextAction = getAlgorithm().getNextActionName();
		MovableAgent.Actions action = MovableAgent.Actions.valueOf(nextAction);

		// increase time since last bomb
		this.timeSinceLastBomb += dt;

		// when the action differs
		if (!currentAction.equals(nextAction)) {
			currentAction = nextAction;
			step = 0;
		}

		if (action != null) {
			// The next action is moving
			move(state, action, dt);
			step = (step + 1) % MAX_MOVEMENT_STEP;
		} else if (currentAction.equals(Bomberman.Actions.PUT_BOMB.toString()) && this.timeSinceLastBomb >= this.timeBetweenBombs) {
			final Position bombPos = new Position(getPosition().xToDiscrete(), getPosition().yToDiscrete());
			state.addAgent(new Bomb(bombPos, explosionRange, explosionTimeout));
			this.timeSinceLastBomb = 0;
		}

		if (currentAction.equals(Agent.Actions.DESTROY.toString())) {
			if (step < MAX_DIE_STEP) {
				step++;
			} else if (step == MAX_DIE_STEP) {
				destroyed = true;
			}
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
			System.out.println("Bomberman#toJson: Error while serializing to json.");
		}
	}

	public enum Actions {
		PUT_BOMB
	}
}
