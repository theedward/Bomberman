package com.cmov.bomberman.model;

import java.util.Map;

public interface PlayerActionListener {
	void draw();

	void onScoreChange(int score);

	void onTimeChange(int time);

	void onNumPlayersChange(int numPlayers);

	void onMapSizeChange(int width, int height);

	void onFinalScores(Map<String, Integer> scores);

	void onDeath();
}
