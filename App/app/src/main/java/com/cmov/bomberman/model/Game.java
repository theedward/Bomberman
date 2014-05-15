package com.cmov.bomberman.model;

public interface Game {
	void start();

	void pause(String username);

	void unpause(String username);

	void quit(String username);

	void join(String username, Player player);
}
