package com.cmov.bomberman.model;

import java.util.Map;

/**
 * Created by JoãoEduardo on 15-04-2014.
 */
public class Controllable implements Algorithm {
    Map<Character, String> keymap;
    Character lastKeyPressed;

    public Controllable(Map<Character, String> keymap) {
        this.keymap = keymap;
    }

    public Map<Character, String> getKeymap() {
        return keymap;
    }

    public void setKeymap(Map<Character, String> keymap) {
        this.keymap = keymap;
    }

    public Character getLastKeyPressed() {
        return lastKeyPressed;
    }

    public void setLastKeyPressed(Character lastKeyPressed) {
        this.lastKeyPressed = lastKeyPressed;
    }

    // Returns true if the key is valid, false otherwise
    public boolean keyPressed(Character c){
        //TODO:Implement this
        return false;
    }

    @Override
    public String getNextActionName() {
        //TODO:Implement this
        return null;
    }
}
