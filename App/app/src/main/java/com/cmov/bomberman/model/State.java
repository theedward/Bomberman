package com.cmov.bomberman.model;

import com.cmov.bomberman.model.agent.Agent;

import java.util.LinkedList;
import java.util.List;

public class State {
    private char[][] map;
    private List<Agent> agents;
    private List<Agent> pausedCharacters;
    private int idCounter;

    public State() {
        agents = new LinkedList<Agent>();
        pausedCharacters = new LinkedList<Agent>();
    }

    /**
     * Sets the last update time to now.
     * This method is called after the game starts.
     */
    public void setLastId(int id) {
        this.idCounter = id;
    }

    public int createNewId() {
        return idCounter++;
    }

    public char[][] getMap() {
        return map;
    }
    public void setMap(final char[][] map) {
        this.map = map;
    }

    /**
     * @return the list of active agents in the state.
     */
    public List<Agent> getObjects() {
        return agents;
    }

    /**
     * This method calls the method play of each agent.
     */
    public void playAll(final long dt) {
        final float dtInSeconds = dt / 1000.0f;

        for (Agent agent : new LinkedList<Agent>(agents)) {
            agent.play(this, dtInSeconds);
        }
    }

    public void addAgent(Agent object) {
        agents.add(object);
    }

    public void removeDestroyedAgents() {
        for (Agent agent : new LinkedList<Agent>(agents)) {
            if (agent.isDestroyed()) {
                destroyAgent(agent);
            }
        }
    }

    public List<Agent> getPausedCharacters() {
        return pausedCharacters;
    }

    /**
     * Adds an agent to be removed from the state in the end of the update
     *
     * @param object the agent to be removed from the state
     */
    public void destroyAgent(Agent object) {
        Position pos = object.getPosition();
        this.map[pos.yToDiscrete()][pos.xToDiscrete()] = DrawingType.EMPTY.toChar();
        this.agents.remove(object);
    }

    /**
     * Adds the agent of the player to the pause list.
     *
     * @param agent the player's agent who's going to be paused
     */
    public void pauseAgent(Agent agent) {
        if (agent != null) {
            agents.remove(agent);
            pausedCharacters.add(agent);
			setMapEntry(agent.getPosition(), DrawingType.EMPTY);
		}
    }

    /**
     * Moves the agent of the player to the agent list.
     * The player must be in pause before.
	 *
	 * @param agent the player's agent who's going to be unpaused.
     */
    public void unpauseAgent(Agent agent) {
        if (agent != null) {
            pausedCharacters.remove(agent);
            agents.add(agent);
			setMapEntry(agent.getPosition(), DrawingType.BOMBERMAN);
		}
    }

    /**
     * @param pos the position
     * @return the agents in the same map position if any exists, otherwise an empty list.
     */
    public List<Agent> getAgentByPosition(Position pos) {
        List<Agent> agentsList = new LinkedList<Agent>();
        for (Agent agent : agents) {
            if (agent.getPosition().equalsInMap(pos)) {
                agentsList.add(agent);
            }
        }
        return agentsList;
    }

	public void setMapEntry(Position position, DrawingType type) {
		map[position.yToDiscrete()][position.xToDiscrete()] = type.toChar();
	}

    public enum DrawingType {
        EMPTY('-'), WALL('W'), ROBOT('R'), BOMBERMAN('M'), OBSTACLE('O'), BOMB('B');

        private char symbol;

        DrawingType(final char symbol) {
            this.symbol = symbol;
        }

        public char toChar() {
            return symbol;
        }
    }
}
