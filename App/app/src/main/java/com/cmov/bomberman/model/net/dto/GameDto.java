package com.cmov.bomberman.model.net.dto;

import com.cmov.bomberman.model.GameConfiguration;
import com.cmov.bomberman.model.Position;
import com.cmov.bomberman.model.State;
import com.cmov.bomberman.model.agent.Bomberman;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class GameDto implements Serializable {
	private int level;
	private Map<String, Bomberman> playersAgent;
	private Map<String, Integer> playerAgentIdx;
	private State gameState;
	private int bombermanIds[];
	private Position bombermanPos[];
	private boolean bombermanUsed[];
	private GameConfiguration gameConfiguration;
	private boolean started;
	private boolean isPaused;
	private List<Position> wallPositions;
	private int numRoundsLeft;
	private Map<String, Boolean> playersState;

	public int getLevel() {
		return level;
	}

	public void setLevel(final int level) {
		this.level = level;
	}

	public Map<String, Bomberman> getPlayersAgent() {
		return playersAgent;
	}

	public void setPlayersAgent(final Map<String, Bomberman> playersAgent) {
		this.playersAgent = playersAgent;
	}

	public Map<String, Integer> getPlayerAgentIdx() {
		return playerAgentIdx;
	}

	public void setPlayerAgentIdx(final Map<String, Integer> playerAgentIdx) {
		this.playerAgentIdx = playerAgentIdx;
	}

	public State getGameState() {
		return gameState;
	}

	public void setGameState(final State gameState) {
		this.gameState = gameState;
	}

	public int[] getBombermanIds() {
		return bombermanIds;
	}

	public void setBombermanIds(final int[] bombermanIds) {
		this.bombermanIds = bombermanIds;
	}

	public Position[] getBombermanPos() {
		return bombermanPos;
	}

	public void setBombermanPos(final Position[] bombermanPos) {
		this.bombermanPos = bombermanPos;
	}

	public boolean[] getBombermanUsed() {
		return bombermanUsed;
	}

	public void setBombermanUsed(final boolean[] bombermanUsed) {
		this.bombermanUsed = bombermanUsed;
	}

	public GameConfiguration getGameConfiguration() {
		return gameConfiguration;
	}

	public void setGameConfiguration(final GameConfiguration gameConfiguration) {
		this.gameConfiguration = gameConfiguration;
	}

	public boolean isStarted() {
		return started;
	}

	public void setStarted(final boolean started) {
		this.started = started;
	}

	public boolean isPaused() {
		return isPaused;
	}

	public void setPaused(final boolean isPaused) {
		this.isPaused = isPaused;
	}

	public List<Position> getWallPositions() {
		return wallPositions;
	}

	public void setWallPositions(final List<Position> wallPositions) {
		this.wallPositions = wallPositions;
	}

	public int getNumRoundsLeft() {
		return numRoundsLeft;
	}

	public void setNumRoundsLeft(final int numRoundsLeft) {
		this.numRoundsLeft = numRoundsLeft;
	}

	public Map<String, Boolean> getPlayersState() {
		return playersState;
	}

	public void setPlayersState(final Map<String, Boolean> playersState) {
		this.playersState = playersState;
	}
}
