package com.cmov.bomberman.model;

import java.util.Map;

/**
 * Created by JoãoEduardo on 15-04-2014.
 */
public class Game {

    Map<String, Player> players;
    State gameState;
    GameConfiguration currentConfig;

    public Map<String, Player> getPlayers() {
        return players;
    }

    public void setPlayers(Map<String, Player> players) {
        this.players = players;
    }

    public State getGameState() {
        return gameState;
    }

    public void setGameState(State gameState) {
        this.gameState = gameState;
    }

    public GameConfiguration getCurrentConfig() {
        return currentConfig;
    }

    public void setCurrentConfig(GameConfiguration currentConfig) {
        this.currentConfig = currentConfig;
    }

    static void readConfigurationFile(String filename){
        //TODO:Implement this
    }

    void addPlayer(Player p){
        //TODO:Implement this
    }

    void removePlayer(Player p){
        //TODO:Implement this
    }

    // Calls onGameStart on each player. Creates the first GameConfiguration.
    void start(){
        //TODO:Implement this
    }

    void end(){
        //TODO:Implement this
    }

    // Updates the state of the map. This is, moves players and detects collisions.
    void update(){
        //TODO:Implement this
    }

    // Calls onGameUpdate
    void pause(String playerUsername){
        //TODO:Implement this
    }

    void unpause (String playerUsername){
        //TODO:Implement this
    }

    void stop(){
        //TODO:Implement this
    }

    void restart(){
        //TODO:Implement this
    }
}
