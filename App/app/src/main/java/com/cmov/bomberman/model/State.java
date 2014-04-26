package com.cmov.bomberman.model;

import com.cmov.bomberman.model.agent.Agent;
import com.cmov.bomberman.model.agent.Bomb;
import com.cmov.bomberman.model.agent.Bomberman;

import java.util.LinkedList;
import java.util.List;


public class State {
	private char[][] map;
	private List<Agent> agents;
	private List<Agent> pausedCharacters;

	/**
	 * The timestamp of the last update.
	 */
	private long lastUpdate;

	public State() {
		agents = new LinkedList<Agent>();
		pausedCharacters = new LinkedList<Agent>();
	}

	/**
	 * Sets the last update time to now.
	 * This method should be called after the game starts.
	 */
	public void startCountingNow() {
		this.lastUpdate = System.currentTimeMillis();
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
	 * Also cleans every object that should be destroyed (isDestroyed == true).
 	 */
 	public void playAll() {
		final long now = System.currentTimeMillis();
		final long dt = now - lastUpdate;
        Position bombermanPosition;

		for (Agent agent : agents) {
			agent.play(this, dt);
			if (agent.isDestroyed()) {
				destroyAgent(agent);
			}
		}


        for (Agent agent : agents) {
            if(agent.getType().equals("Bomberman")) {
                bombermanPosition = agent.getPosition();
                //verify up
                if(map[bombermanPosition.yToDiscrete()+1][bombermanPosition.xToDiscrete()] == 'R')
                    agent.handleEvent(Event.DESTROY);
                else
                //verify down
                if(map[bombermanPosition.yToDiscrete()-1][bombermanPosition.xToDiscrete()] == 'R')
                    agent.handleEvent(Event.DESTROY);
                else
                //verify left
                if(map[bombermanPosition.yToDiscrete()][bombermanPosition.xToDiscrete()-1] == 'R')
                    agent.handleEvent(Event.DESTROY);
                else
                //verify right
                if(map[bombermanPosition.yToDiscrete()][bombermanPosition.xToDiscrete()+1] == 'R')
                    agent.handleEvent(Event.DESTROY);

            }
        }
	}

	public void addAgent(Agent object) {
		agents.add(object);
	}

	public void setMapPosition(Position newPosition, Position oldPosition) {
		char c = map[oldPosition.yToDiscrete()][oldPosition.xToDiscrete()];
		map[oldPosition.yToDiscrete()][oldPosition.xToDiscrete()] = DrawingType.EMPTY.toChar();
		map[newPosition.yToDiscrete()][newPosition.xToDiscrete()] = c;
	}

	public void pauseCharacter(Player player) {
		Agent agent = player.getAgent();
		if (agent != null) {
			agents.remove(agent);
			pausedCharacters.add(agent);
			cleanMapEntry(agent.getPosition());
		}
	}

	public void unPauseCharacter(Player player) {
		Agent agent = player.getAgent();
		if (agent != null) {
			pausedCharacters.remove(agent);
			agents.add(agent);
			addMapEntry(agent.getPosition());
		}
	}

	/*
	* This method removes the given agent from the state
	* */
	public void destroyAgent(Agent object) {
		Position pos = object.getPosition();

		agents.remove(object);
		map[pos.yToDiscrete()][pos.xToDiscrete()] = DrawingType.EMPTY.toChar();
	}

	/*
	* Given a certain explosion range,
	* this method will clear all fields that are in the bomb's path
	* */
	public void bombExplosion(int explosionRange, Bomb bomb) {
		int i;
		float bombPosX = bomb.getPosition().getX();
		float bombPosY = bomb.getPosition().getY();
        Bomberman bombOwner = bomb.getOwner();

		//destroy character in position bomb.pos.line + i
		for (i = 0; i < explosionRange; i++) {
			Position pos = new Position(bombPosX, bombPosY + i);
			Agent agent = getAgentByPosition(pos);
			if (agent != null) {
                if(agent.getType().equals("Robot"))
                    bombOwner.addScore(bombOwner.getRobotScore());
                else if(!agent.equals(bombOwner)) //TODO: Possivelmente esta nao é a melhor verificaçao
                    bombOwner.addScore(bombOwner.getOponentScore());
				agent.handleEvent(Event.DESTROY);
			}
		}
		//destroy character in position bomb.pos.column + i
		for (i = 0; i < explosionRange; i++) {
			Position pos = new Position(bombPosX + i, bombPosY);
			Agent agent = getAgentByPosition(pos);
			if (agent != null) {
                if(agent.getType().equals("Robot"))
                    bombOwner.addScore(bombOwner.getRobotScore());
                else if(!agent.equals(bombOwner))
                    bombOwner.addScore(bombOwner.getOponentScore());
				agent.handleEvent(Event.DESTROY);
			}
		}
		//destroy character in position bomb.pos.line - i
		for (i = 0; i < explosionRange; i++) {
			Position pos = new Position(bombPosX, bombPosY - i);
			Agent agent = getAgentByPosition(pos);
			if (agent != null) {
                if(agent.getType().equals("Robot"))
                    bombOwner.addScore(bombOwner.getRobotScore());
                else if(!agent.equals(bombOwner))
                    bombOwner.addScore(bombOwner.getOponentScore());
				agent.handleEvent(Event.DESTROY);
			}
		}
		//destroy character in position bomb.pos.column - i
		for (i = 0; i < explosionRange; i++) {
			Position pos = new Position(bombPosX - i, bombPosY);
			Agent agent = getAgentByPosition(pos);
			if (agent != null) {
                if(agent.getType().equals("Robot"))
                    bombOwner.addScore(bombOwner.getRobotScore());
                else if(!agent.equals(bombOwner))
                    bombOwner.addScore(bombOwner.getOponentScore());
				agent.handleEvent(Event.DESTROY);
			}
		}
	}

	/**
	 * @param pos the position
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
