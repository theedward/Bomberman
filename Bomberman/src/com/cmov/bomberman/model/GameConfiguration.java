package com.cmov.bomberman.model;

import java.util.Map;

/**
 * Created by JoãoEduardo on 15-04-2014.
 */
public class GameConfiguration {
    // Maps player ids into their initial position in the map.
    Map<Integer, Position> initialPositions;
    int level;
    int numUpdatesPerSecond;
    int numPlayers;
    int timeLeft;

     //TODO: Constructor


    public Map<Integer, Position> getInitialPositions() {
        return initialPositions;
    }

    public void setInitialPositions(Map<Integer, Position> initialPositions) {
        this.initialPositions = initialPositions;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getNumUpdatesPerSecond() {
        return numUpdatesPerSecond;
    }

    public void setNumUpdatesPerSecond(int numUpdatesPerSecond) {
        this.numUpdatesPerSecond = numUpdatesPerSecond;
    }

    public int getNumPlayers() {
        return numPlayers;
    }

    public void setNumPlayers(int numPlayers) {
        this.numPlayers = numPlayers;
    }

    public int getTimeLeft() {
        return timeLeft;
    }

    public void setTimeLeft(int timeLeft) {
        this.timeLeft = timeLeft;
    }
}
