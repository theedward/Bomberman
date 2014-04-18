package com.cmov.bomberman.model;

import java.util.List;
import java.util.Map;

/**
 * Created by JoãoEduardo on 15-04-2014.
 */
public class Player {

    String username;
    int currentScore;
    Screen myScreen;
    List<Agent> objects;

    public Player(String username){
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getCurrentScore() {
        return currentScore;
    }

    public void setCurrentScore(int currentScore) {
        this.currentScore = currentScore;
    }

    public Screen getMyScreen() {
        return myScreen;
    }

    public void setMyScreen(Screen myScreen) {
        this.myScreen = myScreen;
    }

    public List<Agent> getObjects() {
        return objects;
    }

    public void setObjects(List<Agent> objects) {
        this.objects = objects;
    }


    // This method will create all the characters and all the drawables
    void onGameStart(GameConfiguration initialConfig){
    //TODO:Implement this
    }

    // This method will update all the state of the Player.
    // Ex: This will be called when any player pauses the game.
    void onGameUpdate(GameConfiguration currentConfig){
        //TODO:Implement this
    }

    // This method will be called when the game finishes. This can be useful to
    // tell each player who is the winner. (if there's any winner)
    void onGameEnd(GameConfiguration finalConfig){
        //TODO:Implement this
    }

    // This method will be called every round. This will update all the object's positions.
    // don't know if this should replace the whole list
    // or change the positions of matching characters
    // is this supposed to draw each object?

    //void onUpdate(Map<String, Position[]> objectsPositions){
    void onUpdate(List<Agent> agentsToUpdate) {
        setObjects(agentsToUpdate);
        myScreen.drawAll();
    }

}
