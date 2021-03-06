package com.cmov.bomberman.model.agent;

import android.util.JsonWriter;
import android.util.Log;
import com.cmov.bomberman.model.Event;
import com.cmov.bomberman.model.Position;
import com.cmov.bomberman.model.State;

import java.io.IOException;
import java.util.List;

public class Bomb extends Agent {
	private static final int EXPLOSION_MAX_STEP = 7;
	private static final int BOMB_MAX_STEP = 3;

	private final String TAG = this.getClass().getSimpleName();

	private final Bomberman owner;
	private final float explosionDuration;
	private final int range;

	private int rangeRight;
	private int rangeLeft;
	private int rangeUp;
	private int rangeDown;
	private boolean explosion;
	private boolean destroyed;
	private float timeSinceExplosion;

	public Bomb(final Position startingPos, int id, final float explosionDuration, int range, float timeout,
				Bomberman owner) {
		super(startingPos, new BombAlgorithm(timeout), id);
		super.setStep(0);
		this.explosionDuration = explosionDuration;
		this.range = range;
		this.owner = owner;
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
				processExplosion(state);
				explosion = true;
				timeSinceExplosion = 0;
			}
		}

		if (explosion) {
			// during the explosion, the steps displayed should be [0 1 2 3 2 1 0]
			final int[] sprite = new int[] { 0, 1, 2, 3, 2, 1, 0 };
			int step = (int) Math.floor(timeSinceExplosion * sprite.length / explosionDuration);

			if (timeSinceExplosion > explosionDuration) {
				destroyed = true;
			} else {
				super.setStep(sprite[step]);
			}

			timeSinceExplosion += dt;
		} else {
			this.setStep((this.getStep() + 1) % BOMB_MAX_STEP);
		}

		state.setMapEntry(getPosition(), State.DrawingType.BOMB);
	}

	/**
	 * Destroys every enemy that is in the range and removes them from the map.
	 */
	public void processExplosion(final State state) {
		final int bombMapX = Position.toDiscrete(getPosition().getX());
		final int bombMapY = Position.toDiscrete(getPosition().getY());

		final char[][] map = state.getMap();

		// destroy character in position bomb.pos
		Position pos = new Position(bombMapX, bombMapY);
		List<Agent> agentsToDestroy = state.getAgentByPosition(pos);
		for (Agent agent : agentsToDestroy) {
			if (agent != null) {
				if (agent.getType().equals("Robot")) {
					owner.addScore(owner.getRobotScore());
				} else if (agent.getType().equals("Bomberman") && !agent.equals(owner)) {
					owner.addScore(owner.getOpponentScore());
				}
				agent.handleEvent(Event.DESTROY);
			}
		}

		// destroy character in position bomb.pos.line + i
		final char wall = State.DrawingType.WALL.toChar();
		int i;
		boolean destroyedAgent = false;
		for (i = 1; i <= range; i++) {
			if (map[bombMapY + i][bombMapX] == wall) {
				rangeDown = i - 1;
				break;
			} else {
				rangeDown = i;
			}

			agentsToDestroy = state.getAgentByPosition(new Position(bombMapX, bombMapY + i));
			for (Agent agent : agentsToDestroy) {
				if (agent != null) {
					if (agent.getType().equals("Robot")) {
						owner.addScore(owner.getRobotScore());
					} else if (agent.getType().equals("Bomberman") && !agent.equals(owner)) {
						owner.addScore(owner.getOpponentScore());
					}
					agent.handleEvent(Event.DESTROY);
					destroyedAgent = true;
				}
			}

			if (destroyedAgent) {
				break;
			}
		}

		//destroy character in position bomb.pos.column + i
		destroyedAgent = false;
		for (i = 1; i <= range; i++) {
			if (map[bombMapY][bombMapX + i] == wall) {
				rangeRight = i - 1;
				break;
			} else {
				rangeRight = i;
			}

			agentsToDestroy = state.getAgentByPosition(new Position(bombMapX + i, bombMapY));
			for (Agent agent : agentsToDestroy) {
				if (agent != null) {
					if (agent.getType().equals("Robot")) {
						owner.addScore(owner.getRobotScore());
					} else if (agent.getType().equals("Bomberman") && !agent.equals(owner)) {
						owner.addScore(owner.getOpponentScore());
					}
					agent.handleEvent(Event.DESTROY);
					destroyedAgent = true;
				}
			}

			if (destroyedAgent) {
				break;
			}
		}

		//destroy character in position bomb.pos.line - i
		destroyedAgent = false;
		for (i = 1; i <= range; i++) {
			if (map[bombMapY - i][bombMapX] == wall) {
				rangeUp = i - 1;
				break;
			} else {
				rangeUp = i;
			}

			agentsToDestroy = state.getAgentByPosition(new Position(bombMapX, bombMapY - i));
			for (Agent agent : agentsToDestroy) {
				if (agent != null) {
					if (agent.getType().equals("Robot")) {
						owner.addScore(owner.getRobotScore());
					} else if (agent.getType().equals("Bomberman") && !agent.equals(owner)) {
						owner.addScore(owner.getOpponentScore());
					}
					agent.handleEvent(Event.DESTROY);
					destroyedAgent = true;
				}
			}

			if (destroyedAgent) {
				break;
			}
		}

		//destroy character in position bomb.pos.column - i
		destroyedAgent = false;
		for (i = 1; i <= range; i++) {
			if (map[bombMapY][bombMapX - i] == wall) {
				rangeLeft = i - 1;
				break;
			} else {
				rangeLeft = i;
			}

			agentsToDestroy = state.getAgentByPosition(new Position(bombMapX - i, bombMapY));
			for (Agent agent : agentsToDestroy) {
				if (agent != null) {
					if (agent.getType().equals("Robot")) {
						owner.addScore(owner.getRobotScore());
					} else if (agent.getType().equals("Bomberman") && !agent.equals(owner)) {
						owner.addScore(owner.getOpponentScore());
					}
					agent.handleEvent(Event.DESTROY);
					destroyedAgent = true;
				}
			}

			if (destroyedAgent) {
				break;
			}
		}

		state.setMapEntry(getPosition(), State.DrawingType.EMPTY);

		Log.i(TAG, "Bomb range: Left= " + rangeLeft + " Right=" + rangeRight + " Up:" + rangeUp + " Down:" +
				   rangeDown);
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

			writer.name("rangeRight").value(this.rangeRight);
			writer.name("rangeLeft").value(this.rangeLeft);
			writer.name("rangeUp").value(this.rangeUp);
			writer.name("rangeDown").value(this.rangeDown);

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
