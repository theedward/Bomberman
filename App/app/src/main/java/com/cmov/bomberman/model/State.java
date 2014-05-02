package com.cmov.bomberman.model;

import com.cmov.bomberman.model.agent.Agent;

import java.util.LinkedList;
import java.util.List;


public class State {
    private char[][] map;
    private List<Agent> agents;
    private List<Agent> pausedCharacters;
    private int idCounter;

    /**
     * The time of the last update.
     */
    private long lastUpdate;

    public State() {
        agents = new LinkedList<Agent>();
        pausedCharacters = new LinkedList<Agent>();
    }

    /**
     * Sets the last update time to now.
     * This method is called after the game starts.
     */
    public void startCountingNow() {
        this.lastUpdate = System.currentTimeMillis();
    }

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
    public void playAll() {
        final long now = System.currentTimeMillis();
        final float dt = (now - lastUpdate) / 1000.0f;

        for (Agent agent : new LinkedList<Agent>(agents)) {
            agent.play(this, dt);
        }

        this.lastUpdate = System.currentTimeMillis();
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
     * Moves the content of one position to the other position in the map.
     */
    public void setMapPosition(Position newPosition, Position oldPosition) {
        char c = map[oldPosition.yToDiscrete()][oldPosition.xToDiscrete()];
        map[oldPosition.yToDiscrete()][oldPosition.xToDiscrete()] = DrawingType.EMPTY.toChar();
        map[newPosition.yToDiscrete()][newPosition.xToDiscrete()] = c;
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
            cleanMapEntry(agent.getPosition());
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
            addMapEntry(agent.getPosition());
        }
    }

    /**
     * @param pos the position
     * @return the agents in the same map position if any exists, otherwise an empty list.
     */
    public List<Agent> getAgentByPosition(Position pos) {
        List<Agent> agentsList = new LinkedList<Agent>();
        for (Agent agent : agents) {
            if (agent.getPosition().equals(pos)) {
                agentsList.add(agent);
            }
        }
        return agentsList;
    }

    private void cleanMapEntry(Position position) {
        map[position.yToDiscrete()][position.xToDiscrete()] = DrawingType.EMPTY.toChar();
    }

    private void addMapEntry(Position position) {
        map[position.yToDiscrete()][position.xToDiscrete()] = DrawingType.BOMBERMAN.toChar();
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
