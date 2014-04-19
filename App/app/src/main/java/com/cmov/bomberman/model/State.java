package com.cmov.bomberman.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class State {

	public char[][] map;

	/*
	* '-' stands for empty
	* 'W' stands for wall
	* 'M' stands for movable agent
	* 'O' stands for obstacle
	* 'B' stands for bomb
	* */
	private Map<Position, Agent> objects; //this only contains destroyable characters
	private int idCounter;

	public State() {
		objects = new HashMap<Position, Agent>();
		idCounter = 0;
	}

	public int createNewId() {
		return idCounter++;
	}

	public void setMapPosition() {}

	public List<Agent> getObjects() {
		return (List<Agent>) objects.values();
	}

	// This method will call the method play of each player.
	// in the end after all plays must check for possible deaths or
	// objects to be destroyed
	public void playAll() {

		for (Agent character : objects.values()) {
			character.play(this);
			if (character.isDestroyed()) {
				destroyCharacter(character);
			}
		}
	}

	// This method will remove all the given objects from the state.
	// is this needed
	public void removeAll(List<Agent> objs) {

	}

	public void addCharacter(Agent object) {
		objects.put(object.getPosition(), object);
		// must change the map and respective char
		//must add respective map position
	}

	public void destroyCharacter(Agent object) {
		Position pos = object.getPosition();

		objects.remove(object);
		map[pos.yToDiscrete()][pos.xToDiscrete()] = '-';
		//must clean respective map position
	}

	//Given a certain explosion range, this method will clear all fields that are in the bomb's path
	public void bombExplosion(int explosionRange, Bomb bomb) {

		int i;
		float bombPosX = bomb.getPosition().getX();
		float bombPosY = bomb.getPosition().getY();

		//this either can be delayed to watch a certain character die muahahah
		//or it can destroy the characters immediately
		//for now it destroys the characters immediately

		//destroy character in position bomb.pos.line + i
		for (i = 0; i < explosionRange; i++) {
			Position pos = new Position(bombPosX, bombPosY + i);
			if (objects.containsKey(pos)) {
				destroyCharacter(objects.get(pos));
			}
		}
		//destroy character in position bomb.pos.column + i
		for (i = 0; i < explosionRange; i++) {
			Position pos = new Position(bombPosX + i, bombPosY);
			if (objects.containsKey(pos)) {
				destroyCharacter(objects.get(pos));
			}
		}
		//destroy character in position bomb.pos.line - i
		for (i = 0; i < explosionRange; i++) {
			Position pos = new Position(bombPosX, bombPosY - i);
			if (objects.containsKey(pos)) {
				destroyCharacter(objects.get(pos));
			}
		}
		//destroy character in position bomb.pos.column - i
		for (i = 0; i < explosionRange; i++) {
			Position pos = new Position(bombPosX - i, bombPosY);
			if (objects.containsKey(pos)) {
				destroyCharacter(objects.get(pos));
			}
		}
	}

	public enum Character {
		EMPTY('-'), WALL('W'), MOVABLE_AGENT('M'), OBSTACLE('O'), BOMB('B');

		private char symbol;

		Character(final char symbol) {
			this.symbol = symbol;
		}

		char toChar() {
			return symbol;
		}
	}
}
