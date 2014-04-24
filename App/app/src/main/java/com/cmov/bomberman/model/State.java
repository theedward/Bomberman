package com.cmov.bomberman.model;

import com.cmov.bomberman.model.agent.Agent;
import com.cmov.bomberman.model.agent.Bomb;
import com.cmov.bomberman.model.agent.Bomberman;

import java.util.LinkedList;
import java.util.List;


public class State {
	private char[][] map;
	private List<Agent> agents;
	private List<Bomberman> pausedCharacters;
	private int mapWidth;
	private int mapHeight;

	public State() {
		agents = new LinkedList<Agent>();
		pausedCharacters = new LinkedList<Bomberman>();
	}

	public char[][] getMap() {
		return map;
	}

	public void setMap(final char[][] map) {
		this.map = map;
		this.mapHeight = map.length;
		this.mapWidth = (this.mapHeight > 0) ? map[0].length : 0;
	}

	public int getMapWidth() {
		return mapWidth;
	}

	public int getMapHeight() {
		return mapHeight;
	}

	/*
				* This method returns the list of agents in the state
				* */
	public List<Agent> getObjects() {
		return agents;
	}

	/*
	* This method will call the method play of each player.
	* in the end after all plays must check for possible deaths or
	* objects to be destroyed
	* */
	public void playAll() {

		// TODO verify when the bomberman has robots at distance 1

		for (Agent agent : agents) {
			agent.play(this, );
			if (agent.isDestroyed()) {
				destroyAgent(agent);
			}
		}
	}

	/*
	* This method will remove all the given objects from the state.
	* is this needed
	* */
	public void removeAll(List<Agent> objs) {
		agents.removeAll(objs);
	}

	/*
	* This method adds the given agent to the state
	* */
	public void addAgent(Agent object) {
		agents.add(object);
	}

	public void setMapPosition(Position newPosition, Position oldPosition) {
		char character = map[oldPosition.yToDiscrete()][oldPosition.yToDiscrete()];

		map[oldPosition.yToDiscrete()][oldPosition.yToDiscrete()] = DrawingType.EMPTY.toChar();
		map[newPosition.yToDiscrete()][newPosition.yToDiscrete()] = character;
	}

	public void pauseCharacter(String ownerUsername) {
		Bomberman bmCharacter = getAgentByOwner(ownerUsername);
		if (bmCharacter != null) {
			agents.remove(bmCharacter);
			pausedCharacters.add(bmCharacter);
			cleanMapEntry(bmCharacter.getPosition());
		}
	}

	public void unPauseCharacter(String ownerUsername) {
		Bomberman bmCharacter = getAgentByOwner(ownerUsername);
		if (bmCharacter != null) {
			pausedCharacters.remove(bmCharacter);
			agents.add(bmCharacter);
			addMapEntry(bmCharacter.getPosition());
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

		//destroy character in position bomb.pos.line + i
		for (i = 0; i < explosionRange; i++) {
			Position pos = new Position(bombPosX, bombPosY + i);
			Agent agent = getAgentByPosition(pos);
			if (agent != null) {
				agent.handleEvent(Event.DESTROY);
			}
		}
		//destroy character in position bomb.pos.column + i
		for (i = 0; i < explosionRange; i++) {
			Position pos = new Position(bombPosX + i, bombPosY);
			Agent agent = getAgentByPosition(pos);
			if (agent != null) {
				agent.handleEvent(Event.DESTROY);
			}
		}
		//destroy character in position bomb.pos.line - i
		for (i = 0; i < explosionRange; i++) {
			Position pos = new Position(bombPosX, bombPosY - i);
			Agent agent = getAgentByPosition(pos);
			if (agent != null) {
				agent.handleEvent(Event.DESTROY);
			}
		}
		//destroy character in position bomb.pos.column - i
		for (i = 0; i < explosionRange; i++) {
			Position pos = new Position(bombPosX - i, bombPosY);
			Agent agent = getAgentByPosition(pos);
			if (agent != null) {
				agent.handleEvent(Event.DESTROY);
			}
		}
	}

	/*
	* This method returns an Agent that matches the given position
	* or null if theres none
	* */
	private Agent getAgentByPosition(Position pos) {
		for (Agent agent : agents) {
			if (agent.getPosition().equals(pos)) {
				return agent;
			}
		}
		return null;
	}

	private Bomberman getAgentByOwner(String ownerUsername) {
		Bomberman bomberman = null;

		for (Agent agent : agents) {
			if (agent.getType().equals("Bomberman")) {
				bomberman = (Bomberman) agent;
				if (bomberman.hasOwnerWithUsername(ownerUsername)) {
					return bomberman;
				}
			}
		}
		return bomberman;
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
