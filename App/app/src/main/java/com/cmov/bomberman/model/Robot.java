package com.cmov.bomberman.model;

import android.util.JsonWriter;
import java.io.IOException;

public class Robot extends MovableAgent {

	private static final int MAX_MOVEMENT_STEP = 1;
	private static final int MAX_DIE_STEP = 5;


	private boolean isDestroyed;
	private String currentAction;

    private int step;

	public Robot(final Position position, final int speed, String type) {
		super(position, new Algorithm() {
            private boolean destroyMode;

            @Override
            public String getNextActionName() {
                int nextMove = (int) Math.random() % 4;

                if(nextMove == 0) {
                    return MovableAgentActions.MOVE_BOTTOM.toString();
                } else if(nextMove == 1) {
                    return MovableAgentActions.MOVE_TOP.toString();
                } else if(nextMove == 2) {
                    return MovableAgentActions.MOVE_LEFT.toString();
                } else if(nextMove == 3) {
                    return MovableAgentActions.MOVE_RIGHT.toString();
                }
                return null;
            }

            @Override
            public void handleEvent(Event e) {
                if (e == Event.DESTROY) {
                    destroyMode = true;
                }
            }
        }, speed, type);
	}

	@Override
	public void play(State state) {

        String nextAction = getAlgorithm().getNextActionName();

        Move move = ActionToMove(nextAction);
        move(state, move);

        if (!currentAction.equals(nextAction)) {
            currentAction = nextAction;
            step = 0;
        } else if (currentAction.equals(AgentActions.DESTROY)) {
            if (step < MAX_DIE_STEP) {
                step++;
            } else if (step == MAX_DIE_STEP) {
                isDestroyed = true;
                return;
            }
        }
	}

	@Override
	public boolean isDestroyed() {
		return isDestroyed;
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
            writer.name("step").value(0);
            writer.endObject();
        }
        catch (IOException e) {

        }
    }
}
