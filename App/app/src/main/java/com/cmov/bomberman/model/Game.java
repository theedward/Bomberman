package com.cmov.bomberman.model;

import java.util.Collection;

public interface Game {
	void start();
	void pause();
	void unpause();

	void pause(String username);
	void unpause(String username);
	void quit(String username);
	void join(String username, Player player);

	Collection<String> getPlayerUsernames();
	int getMapWidth();
	int getMapHeight();
}
