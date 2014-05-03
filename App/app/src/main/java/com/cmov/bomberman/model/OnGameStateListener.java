package com.cmov.bomberman.model;

import java.util.Collection;

public interface OnGameStateListener {
	void onGameStart();
	void onGameUpdate(Collection<String> playerUsernames);
	void onGameEnd();
}
