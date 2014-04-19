package com.cmov.bomberman.model;

import java.util.LinkedList;
import java.util.List;


public class State {


    /*
    * '-' stands for empty
    * 'W' stands for wall
    * 'M' stands for Bomberman
    * 'R' stands for Robot
    * 'O' stands for obstacle
    * 'B' stands for bomb
    * */
    public char[][] map;
    private List<Agent> agents;

	public State() {
		agents = new LinkedList<Agent>();
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

		for (Agent agent : agents) {
			agent.play(this);
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

    /*
    * This method removes the given agent from the state
    * */
	public void destroyAgent(Agent object) {
		Position pos = object.getPosition();

		agents.remove(object);
		map[pos.yToDiscrete()][pos.xToDiscrete()] = Character.EMPTY.toChar();
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

	public enum Character {
		EMPTY('-'), WALL('W'), ROBOT('R'), BOMBERMAN('M'), OBSTACLE('O'), BOMB('B');

		private char symbol;

		Character(final char symbol) {
			this.symbol = symbol;
		}

		char toChar() {
			return symbol;
		}
	}

    /*
    * This method returns an Agent that matches the given position
    * or null if theres none
    * */
    private Agent getAgentByPosition(Position pos){
        for(Agent agent: agents) {
            if(agent.getPosition().equals(pos)) {
                return agent;
            }
        }
        return null;
    }
}
