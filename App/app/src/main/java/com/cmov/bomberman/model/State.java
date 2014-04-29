package com.cmov.bomberman.model;

import com.cmov.bomberman.model.agent.Agent;
import com.cmov.bomberman.model.agent.Bomb;
import com.cmov.bomberman.model.agent.Bomberman;

import java.util.LinkedList;
import java.util.List;


public class State {
	private char[][] map;
	private List<Agent> agents;
	private List<Agent> agentsToAdd;
	private List<Agent> agentsToRemove;
	private List<Agent> pausedCharacters;
	private int objectsIdCounter;
    private int bombLimitLeft = 1;
    private int bombLimitRight = 1;
    private int bombLimitUp = 1;
    private int bombLimitDown = 1;


	/**
	 * The timestamp of the last update.
	 */
	private long lastUpdate;

	public State() {
		agents = new LinkedList<Agent>();
		agentsToAdd = new LinkedList<Agent>();
		agentsToRemove = new LinkedList<Agent>();
		pausedCharacters = new LinkedList<Agent>();
	}

	/**
	 * Sets the last update time to now.
	 * This method should be called after the game starts.
	 */
	public void startCountingNow() {
		this.lastUpdate = System.currentTimeMillis();
	}

	public void setObjectsIdCounter(int objectsIdCounter) {
		this.objectsIdCounter = objectsIdCounter;
	}

	public int incObjectsIdCounter() {
		return objectsIdCounter++;
	}

	public char[][] getMap() {
		return map;
	}

	public void setMap(final char[][] map) {
		this.map = map;
	}

    public void setBombLimitRight(int bombLimitRight) { this.bombLimitRight = bombLimitRight; }

    public void setBombLimitLeft(int bombLimitLeft){ this.bombLimitLeft = bombLimitLeft; }

    public void setBombLimitUp (int bombLimitUp) { this.bombLimitUp = bombLimitUp; }

    public void setBombLimitDown (int bombLimitDown) { this.bombLimitDown = bombLimitDown; }

    public int getBombLimitRight () { return this.bombLimitRight; }

    public int getBombLimitLeft () { return this.bombLimitLeft; }

    public int getBombLimitUp () { return this.bombLimitUp; }

    public int getBombLimitDown () { return this.bombLimitDown; }
	/**
	 * @return the list of active agents in the state.
	 */
	public List<Agent> getObjects() {
		return agents;
	}

	/**
	 * This method calls the method play of each agent.
	 * Also cleans every object that should be destroyed (isDestroyed == true).
	 */
	public void playAll() {
		final long now = System.currentTimeMillis();
		final float dt = (now - lastUpdate) / 1000.0f;

        for (Agent agent : new LinkedList<Agent>(agents)) {
            agent.play(this, dt);
        }

		// Check if any robot is in an adjacent position to a Bomberman
		final int maxY = map.length;
		final int maxX = map[0].length;
		for (Agent agent : agents) {
			if (agent.getType().equals("Bomberman")) {
				final int mapX = Position.toDiscrete(agent.getPosition().getX());
				final int mapY = Position.toDiscrete(agent.getPosition().getY());
				if ((mapY + 1 < maxY && map[mapY + 1][mapX] == 'R') ||
					(mapY - 1 >= 0 && map[mapY - 1][mapX] == 'R') ||
					(mapX - 1 >= 0 && map[mapY][mapX - 1] == 'R') ||
					(mapX + 1 < maxX && map[mapY][mapX + 1] == 'R')) {
					agent.handleEvent(Event.DESTROY);
				}
			}
		}

		// remove new agents in the other agents list
		agents.removeAll(agentsToRemove);
		agentsToRemove.clear();

		// insert new agents in the other agents list
		agents.addAll(agentsToAdd);
		agentsToAdd.clear();

		this.lastUpdate = now;
	}

	public void addAgent(Agent object) {
		agents.add(object);
	}

	public void addAgentDuringUpdate(Agent a) {
		this.agentsToAdd.add(a);
	}

	public void removeDestroyedAgents() {
		for (Agent agent : agents) {
			if (agent.isDestroyed()) {
				destroyAgent(agent);
			}
		}
	}

	/**
	 * Adds an agent to be removed from the state in the end of the update
	 *
	 * @param object the agent to be removed from the state
	 */
	public void destroyAgent(Agent object) {
		Position pos = object.getPosition();
		this.map[pos.yToDiscrete()][pos.xToDiscrete()] = DrawingType.EMPTY.toChar();
		this.agentsToRemove.add(object);
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
	 * @param player the player whose agent is going to be paused
	 */
	public void pauseAgent(Player player) {
		Agent agent = player.getAgent();
		if (agent != null) {
			agents.remove(agent);
			pausedCharacters.add(agent);
			cleanMapEntry(agent.getPosition());
		}
	}

	/**
	 * Moves the agent of the player to the agent list.
	 * The player must be in pause before.
	 */
	public void unpauseAgent(Player player) {
		Agent agent = player.getAgent();
		if (agent != null) {
			pausedCharacters.remove(agent);
			agents.add(agent);
			addMapEntry(agent.getPosition());
		}
	}

	/**
	 * TODO: move this to the bomb class
	 * <p/>
	 * Given a certain explosion range,
	 * this method will clear all fields that are in the bomb's path
	 */
	public void bombExplosion(int explosionRange, Bomb bomb) {
        float bombPosX = bomb.getPosition().getX();
		float bombPosY = bomb.getPosition().getY();
		Bomberman bombOwner = bomb.getOwner();

		// destroy character in position bomb.pos
		Position pos = new Position(bombPosX, bombPosY);
		Agent agent = getAgentByPosition(pos);
		if (agent != null) {
			if (agent.getType().equals("Robot")) {
				bombOwner.addScore(bombOwner.getRobotScore());
			} else if (agent.getType().equals("Bomberman") && !agent.equals(bombOwner)) {
				bombOwner.addScore(bombOwner.getOponentScore());
			}
			agent.handleEvent(Event.DESTROY);
		}

		// destroy character in position bomb.pos.line + i
		int i;
		for (i = 0; i <= explosionRange; i++) {
			pos = new Position(bombPosX, bombPosY + i);
            if(map[pos.yToDiscrete()][pos.xToDiscrete()] == 'W') {
                setBombLimitRight(i);
                break;
            }
            setBombLimitRight(explosionRange);
			agent = getAgentByPosition(pos);
			if (agent != null) {
				if (agent.getType().equals("Robot")) {
					bombOwner.addScore(bombOwner.getRobotScore());
				} else if (agent.getType().equals("Bomberman") && !agent.equals(bombOwner)) {
					bombOwner.addScore(bombOwner.getOponentScore());
				}
				agent.handleEvent(Event.DESTROY);
			}
		}
		//destroy character in position bomb.pos.column + i
		for (i = 0; i <= explosionRange; i++) {
			pos = new Position(bombPosX + i, bombPosY);
            if(map[pos.yToDiscrete()][pos.xToDiscrete()] == 'W') {
                setBombLimitUp(i);
                break;
            }
            setBombLimitUp(explosionRange);
			agent = getAgentByPosition(pos);
			if (agent != null) {
				if (agent.getType().equals("Robot")) {
					bombOwner.addScore(bombOwner.getRobotScore());
				} else if (agent.getType().equals("Bomberman") && !agent.equals(bombOwner)) {
					bombOwner.addScore(bombOwner.getOponentScore());
				}
				agent.handleEvent(Event.DESTROY);
			}
		}
		//destroy character in position bomb.pos.line - i
		for (i = 0; i <= explosionRange; i++) {
			pos = new Position(bombPosX, bombPosY - i);
            if(map[pos.yToDiscrete()][pos.xToDiscrete()] == 'W') {
                setBombLimitLeft(i);
                break;
            }
            setBombLimitLeft(explosionRange);
			agent = getAgentByPosition(pos);
			if (agent != null) {
				if (agent.getType().equals("Robot")) {
					bombOwner.addScore(bombOwner.getRobotScore());
				} else if (agent.getType().equals("Bomberman") && !agent.equals(bombOwner)) {
					bombOwner.addScore(bombOwner.getOponentScore());
				}
				agent.handleEvent(Event.DESTROY);
			}
		}
		//destroy character in position bomb.pos.column - i
		for (i = 0; i <= explosionRange; i++) {
			pos = new Position(bombPosX - i, bombPosY);
            if(map[pos.yToDiscrete()][pos.xToDiscrete()] == 'W') {
                setBombLimitDown(i);
                System.out.println("FOUND A WALL: " + bombLimitDown);
                break;
            }
            setBombLimitDown(explosionRange);
			agent = getAgentByPosition(pos);
			if (agent != null) {
				if (agent.getType().equals("Robot")) {
					bombOwner.addScore(bombOwner.getRobotScore());
				} else if (agent.getType().equals("Bomberman") && !agent.equals(bombOwner)) {
					bombOwner.addScore(bombOwner.getOponentScore());
				}
				agent.handleEvent(Event.DESTROY);
			}
		}
	}

	/**
	 * TODO: More than one agent can be in the same position!
	 *
	 * @param pos the position
	 *
	 * @return the agent in that position if exists, null otherwise.
	 */
	private Agent getAgentByPosition(Position pos) {
		for (Agent agent : agents) {
			if (agent.getPosition().equals(pos)) {
				return agent;
			}
		}
		return null;
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
