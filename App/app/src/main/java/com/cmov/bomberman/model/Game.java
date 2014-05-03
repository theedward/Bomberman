package com.cmov.bomberman.model;

public interface Game {
	public void start();
	public void pause(String username);
	public void unpause(String username);
	public void quit(String username);
	public void join(String username, Player player);

	public int getMapWidth();
	public int getMapHeight();
}
