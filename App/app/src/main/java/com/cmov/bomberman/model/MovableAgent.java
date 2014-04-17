package com.cmov.bomberman.model;

abstract class MovableAgent extends Agent {

    public enum Move {
        UP, RIGHT, DOWN, LEFT
    };

    // possible collisions
    // with obstacle or with character
    public enum Collision {
        WOBSTACLE, WCHARACTER
    };

    int speed;

    public MovableAgent(Position pos, Algorithm ai, int sp) {
        super(pos,ai);
        speed = sp;
    }
    // Returns type of potential collision or null if there s none
    public Collision move(State currentState, MovableAgent.Move direction) {
        return null;
    }
}