package com.cmov.bomberman.model;

import java.util.List;


public class State {
    public char[][] map;
    private List<Agent> objects;

    // this method will populate the maze
    public void init() {

    }
    // This method will call the method play of each player.
    // in the end after all plays must check for possible deaths or
    // objects to be destroyed
    public void playAll() {

        for(Agent character : objects) {
            character.play(this);
            if (character.isDestroyed()) {
                destroyCharacter(character);
            }
        }
    }

    // This method will remove all the given objects from the state.
    public void removeAll(List<Agent> objects) {}

    public void addCharacter(Agent object) { }
    public void destroyCharacter(Agent object) {}
}