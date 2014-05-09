package com.cmov.bomberman.model.agent;

import android.util.JsonWriter;
import com.cmov.bomberman.model.Event;
import com.cmov.bomberman.model.Position;
import com.cmov.bomberman.model.State;

import java.io.IOException;

public class Bomberman extends MovableAgent {
    private static final int MAX_MOVEMENT_STEP = 3;
    private static final int MAX_DIE_STEP = 3;

    private final float timeBetweenBombs;
    private final int explosionRange;
    private final int explosionTimeout;

    /**
     * This is initialized with time between bombs because a player
     * is allowed to put a bomb right in the beginning.
     */
    private float timeSinceLastBomb;
    private boolean destroyed;
    private int score;
    private int robotScore;
    private int opponentScore;

    /**
     * @param pos              the agent position
     * @param ai               the agent algorithm
     * @param speed            the agent speed
     * @param timeBetweenBombs time between bombs in milliseconds.
     * @param range            the bomb range
     * @param timeout          the bomb timeout
     */
    public Bomberman(Position pos, Algorithm ai, int id, int speed, float timeBetweenBombs, int range, int timeout,
                     int robotScore, int opponentScore) {
        super(pos, ai, id, speed);
        this.timeBetweenBombs = timeBetweenBombs;
        this.timeSinceLastBomb = this.timeBetweenBombs;
        this.explosionRange = range;
        this.explosionTimeout = timeout;
        this.setStep(0);
        this.setLastStep(0);
        this.destroyed = false;
		this.score = 0;
        this.robotScore = robotScore;
        this.opponentScore = opponentScore;
    }


    public void addScore(int score) {
        this.score += score;
    }

	public int getScore() {
		return score;
	}

	public int getRobotScore() {
        return this.robotScore;
    }

    public int getOpponentScore() {
        return this.opponentScore;
    }

    @Override
    public boolean isDestroyed() {
        return destroyed;
    }

    @Override
    public void play(State state, final float dt) {
        // increase time since last bomb
        this.timeSinceLastBomb += dt;

		// perform action
        final String nextAction = getAlgorithm().getNextActionName();
        // when the action differs
        if (!this.getCurrentAction().equals(nextAction)) {
            this.setLastAction(this.getCurrentAction());
            this.setCurrentAction(nextAction);
            this.setLastStep(this.getStep());
            this.setStep(0);
        }

        if (MovableAgent.Actions.isMovableAction(nextAction)) {
            // The next action is moving
            MovableAgent.Actions action = MovableAgent.Actions.valueOf(nextAction);
            move(state, action, dt);
            setStep((this.getStep() + 1) % MAX_MOVEMENT_STEP);
        } else if (this.getCurrentAction().equals(Bomberman.Actions.PUT_BOMB.toString()) &&
                this.timeSinceLastBomb >= this.timeBetweenBombs) {
            final Position curPos = getPosition();
            final Position bombPos = new Position(Position.toDiscrete(curPos.getX()) + Agent.WIDTH / 2,
                    Position.toDiscrete(curPos.getY()) + Agent.HEIGHT / 2);
            final int id = state.createNewId();
            int bombPosX = bombPos.xToDiscrete();
            int bombPosY = bombPos.yToDiscrete();
            state.addAgent(new Bomb(bombPos, id, explosionRange, explosionTimeout, this));
            state.changeMapPosition(bombPosX, bombPosY, State.DrawingType.BOMB.toChar());
            System.out.println("BOMBERMAN: MAP CHANGED");
            System.out.println("THE BOMB HAS BEEN PLANTED!!!!");
            this.timeSinceLastBomb = 0;
        } else if (this.getCurrentAction().equals(Agent.Actions.DESTROY.toString())) {
            if (this.getStep() < MAX_DIE_STEP) {
                setStep(this.getStep() + 1);
            }

            if (this.getStep() == MAX_DIE_STEP) {
                destroyed = true;
            }
        }

		// verify if is going to die
		if (nearRobots(state)) {
			this.handleEvent(Event.DESTROY);
		}
    }

	private boolean nearRobots(final State state) {
		final char[][] map = state.getMap();

		final int maxY = map.length;
		final int maxX = map[0].length;
		final int mapX = Position.toDiscrete(getPosition().getX());
		final int mapY = Position.toDiscrete(getPosition().getY());
		final char robotChar = State.DrawingType.ROBOT.toChar();

		return ((mapY + 1 < maxY && map[mapY + 1][mapX] == robotChar) ||
			(mapY - 1 >= 0 && map[mapY - 1][mapX] == robotChar) ||
			(mapX - 1 >= 0 && map[mapY][mapX - 1] == robotChar) ||
			(mapX + 1 < maxX && map[mapY][mapX + 1] == robotChar));
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
            writer.name("isDestroyed").value(isDestroyed());
            writer.endObject();
        } catch (IOException e) {
            System.out.println("Bomberman#toJson: Error while serializing to json.");
        }
    }

    public enum Actions {
        PUT_BOMB
    }
}
