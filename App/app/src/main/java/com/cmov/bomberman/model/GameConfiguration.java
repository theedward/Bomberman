package com.cmov.bomberman.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by JoãoEduardo on 15-04-2014.
 */
public class GameConfiguration {
	private Map<String, Integer> playerCharacterId;
	private Map<String, Integer> playerScore;
	private Map<Integer, Position> characterPositions;
	private int numUpdatesPerSecond;
	private int maxNumPlayers;
	private int timeLimit;

	// Bomberman variables
	private int bSpeed;

	// Robot variables
	private int rSpeed;

	// Bomb variables
	private int timeToExplode;
	private int explosionDuration;
	private int explosionRange;

	// Points variables
	private int pointRobot;
	private int pointOpponent;

	public GameConfiguration() {
		playerCharacterId = new HashMap<String, Integer>();
		playerScore = new HashMap<String, Integer>();
		characterPositions = new HashMap<Integer, Position>();
	}

	public int getNumUpdatesPerSecond() {
		return numUpdatesPerSecond;
	}

	public void setNumUpdatesPerSecond(final int numUpdatesPerSecond) {
		this.numUpdatesPerSecond = numUpdatesPerSecond;
	}

	public int getMaxNumPlayers() {
		return maxNumPlayers;
	}

	public void setMaxNumPlayers(final int maxNumPlayers) {
		this.maxNumPlayers = maxNumPlayers;
	}

	public int getTimeLimit() {
		return timeLimit;
	}

	public void setTimeLimit(final int timeLimit) {
		this.timeLimit = timeLimit;
	}

	public int getbSpeed() {
		return bSpeed;
	}

	public void setbSpeed(final int bSpeed) {
		this.bSpeed = bSpeed;
	}

	public int getrSpeed() {
		return rSpeed;
	}

	public void setrSpeed(final int rSpeed) {
		this.rSpeed = rSpeed;
	}

	public int getTimeToExplode() {
		return timeToExplode;
	}

	public void setTimeToExplode(final int timeToExplode) {
		this.timeToExplode = timeToExplode;
	}

	public int getExplosionDuration() {
		return explosionDuration;
	}

	public void setExplosionDuration(final int explosionDuration) {
		this.explosionDuration = explosionDuration;
	}

	public int getExplosionRange() {
		return explosionRange;
	}

	public void setExplosionRange(final int explosionRange) {
		this.explosionRange = explosionRange;
	}

	public int getPointRobot() {
		return pointRobot;
	}

	public void setPointRobot(final int pointRobot) {
		this.pointRobot = pointRobot;
	}

	public int getPointOpponent() {
		return pointOpponent;
	}

	public void setPointOpponent(final int pointOpponent) {
		this.pointOpponent = pointOpponent;
	}
}
