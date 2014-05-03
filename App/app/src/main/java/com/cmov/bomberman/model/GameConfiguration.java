package com.cmov.bomberman.model;

import com.cmov.bomberman.model.drawing.Drawing;
import com.cmov.bomberman.model.drawing.WallDrawing;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class GameConfiguration {
    private final List<WallDrawing> wallDrawings;
    private final Map<Integer, Drawing> mutableDrawings;

    private int mapWidth;
    private int mapHeight;
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
    private float timeBetweenBombs;

    // Points variables
    private int pointRobot;
    private int pointOpponent;

    public GameConfiguration() {
        wallDrawings = new LinkedList<WallDrawing>();
        mutableDrawings = new HashMap<Integer, Drawing>();
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
