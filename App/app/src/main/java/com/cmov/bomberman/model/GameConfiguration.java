package com.cmov.bomberman.model;

import java.io.Serializable;

public class GameConfiguration implements Serializable{
	private int mapWidth;
	private int mapHeight;
	private int numUpdatesPerSecond;
	private int maxNumPlayers;
	private int timeLimit;

	// Bomberman variables
	private float bSpeed;

	// Robot variables
	private float rSpeed;

	// Bomb variables
	private float timeToExplode;
	private int explosionRange;
	private float timeBetweenBombs;
	private float explosionDuration;

	// Points variables
	private int pointRobot;
	private int pointOpponent;

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

	public float getbSpeed() {
		return bSpeed;
	}

	public void setbSpeed(final float bSpeed) {
		this.bSpeed = bSpeed;
	}

	public float getrSpeed() {
		return rSpeed;
	}

	public void setrSpeed(final float rSpeed) {
		this.rSpeed = rSpeed;
	}

	public float getTimeToExplode() {
		return timeToExplode;
	}

	public void setTimeToExplode(final float timeToExplode) {
		this.timeToExplode = timeToExplode;
	}

	public int getExplosionRange() {
		return explosionRange;
	}

	public void setExplosionRange(final int explosionRange) {
		this.explosionRange = explosionRange;
	}

	public float getExplosionDuration() {
		return explosionDuration;
	}

	public void setExplosionDuration(final float explosionDuration) {
		this.explosionDuration = explosionDuration;
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

	public float getTimeBetweenBombs() {
		return timeBetweenBombs;
	}

	public void setTimeBetweenBombs(final float timeBetweenBombs) {
		this.timeBetweenBombs = timeBetweenBombs;
	}

	public int getMapWidth() {
		return mapWidth;
	}

	public void setMapWidth(final int mapWidth) {
		this.mapWidth = mapWidth;
	}

	public int getMapHeight() {
		return mapHeight;
	}

	public void setMapHeight(final int mapHeight) {
		this.mapHeight = mapHeight;
	}
}
