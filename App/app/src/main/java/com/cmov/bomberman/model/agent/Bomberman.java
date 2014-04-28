package com.cmov.bomberman.model.agent;

import android.util.JsonWriter;
import android.util.Log;

import com.cmov.bomberman.model.Position;
import com.cmov.bomberman.model.State;

import java.io.IOException;

public class Bomberman extends MovableAgent {
	private static final int MAX_MOVEMENT_STEP = 3;
	private static final int MAX_DIE_STEP = 3;

	private final int timeBetweenBombs;
	private final int explosionRange;
	private final int explosionTimeout;
	/**
	 * This is initialized with time between bombs because a player
	 * is allowed to put a bomb right in the beginning.
	 */
	private long timeSinceLastBomb;
	private boolean destroyed;
	private int score = 0;
	private int robotScore;
	private int oponentScore;

	/**
	 * @param pos              the agent position
	 * @param ai               the agent algorithm
	 * @param speed            the agent speed
	 * @param timeBetweenBombs time between bombs in milliseconds.
	 * @param range            the bomb range
	 * @param timeout          the bomb timeout
	 */
	public Bomberman(Position pos, Algorithm ai, int id, int speed, int timeBetweenBombs, int range, int timeout,
					 int robotScore, int oponentScore) {
		super(pos, ai, id, speed);
		this.timeBetweenBombs = timeBetweenBombs * 1000;
		this.timeSinceLastBomb = this.timeBetweenBombs;
		this.explosionRange = range;
		this.explosionTimeout = timeout;
		this.setStep(0);
		this.setLastStep(0);
		this.destroyed = false;
		this.robotScore = robotScore;
		this.oponentScore = oponentScore;
	}


	public void addScore(int score) {
		this.score += score;
	}

	public int getRobotScore() {
		return this.robotScore;
	}

	public int getOponentScore() {
		return this.oponentScore;
	}

	@Override
	public boolean isDestroyed() {
		return destroyed;
	}

	@Override
	public void play(State state, final float dt) {
		// increase time since last bomb
		this.timeSinceLastBomb += dt;

		String nextAction = getAlgorithm().getNextActionName();

		// when the action differs
		if (!this.getCurrentAction().equals(nextAction)) {
			this.setLastAction(this.getCurrentAction());
			this.setCurrentAction(nextAction);
			this.setLastStep(this.getStep());
			this.setStep(0);
		}

		if (nextAction.equals(MovableAgent.Actions.MOVE_LEFT.toString()) ||
			nextAction.equals(MovableAgent.Actions.MOVE_UP.toString()) ||
			nextAction.equals(MovableAgent.Actions.MOVE_RIGHT.toString()) ||
			nextAction.equals(MovableAgent.Actions.MOVE_DOWN.toString())) {
			// The next action is moving
			MovableAgent.Actions action = MovableAgent.Actions.valueOf(nextAction);
			move(state, action, dt);
			setStep((this.getStep() + 1) % MAX_MOVEMENT_STEP);
		} else if (this.getCurrentAction().equals(Bomberman.Actions.PUT_BOMB.toString()) &&
				   this.timeSinceLastBomb >= this.timeBetweenBombs) {
			final Position curPos = getPosition();
			final Position bombPos = new Position(Position.toDiscrete(curPos.getX()) + Agent.WIDTH / 2,
												  Position.toDiscrete(curPos.getY()) + Agent.HEIGHT / 2);
			final int id = state.incObjectsIdCounter();
			state.addAgent(new Bomb(bombPos, id, explosionRange, explosionTimeout, this));

			this.timeSinceLastBomb = 0;
		} else if (this.getCurrentAction().equals(Agent.Actions.DESTROY.toString())) {
			if (this.getStep() < MAX_DIE_STEP) {
				setStep(this.getStep() + 1);
			}

            if (this.getStep() == MAX_DIE_STEP) {
				destroyed = true;
                Log.i("Bomberman", "was destroyed");
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

			writer.name("currentAction").value(this.getCurrentAction());
			writer.name("lastAction").value(this.getLastAction());
			writer.name("step").value(this.getStep());
			writer.name("lastStep").value(this.getLastStep());
			writer.name("id").value(getId());
			writer.name("score").value(score);
			writer.name("isDestroyed").value(isDestroyed());
			writer.endObject();

            Log.i("Bomberman", "toJson " + destroyed);
		}
		catch (IOException e) {
			System.out.println("Bomberman#toJson: Error while serializing to json.");
		}
	}

	public enum Actions {
		PUT_BOMB
	}
}
