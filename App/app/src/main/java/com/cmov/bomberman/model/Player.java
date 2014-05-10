package com.cmov.bomberman.model;

import com.cmov.bomberman.model.agent.Algorithm;

import java.util.List;
import java.util.Map;

public interface Player {
	void update(String msg);
	Algorithm getController();
	void onGameStart(final int level, final List<Position> wallPositions);
	void onGameEnd(final Map<String, Integer> scores);
}
