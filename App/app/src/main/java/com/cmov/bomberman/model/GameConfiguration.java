package com.cmov.bomberman.model;

public class GameConfiguration {
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
    private int timeToExplode;
    private int explosionRange;
    private float timeBetweenBombs;

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

    public int getTimeToExplode() {
        return timeToExplode;
    }

    public void setTimeToExplode(final int timeToExplode) {
        this.timeToExplode = timeToExplode;
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
