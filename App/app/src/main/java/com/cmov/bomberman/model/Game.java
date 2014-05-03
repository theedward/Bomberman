package com.cmov.bomberman.model;

public interface Game {
	public void registerPlayer(String username, Player player);
	public void pause(String username);
	public void unpause(String username);
	public void quit(String username);
	public void join(String username, Player player);
	public void begin();
	public void end();
	public void update();

	public int getMapWidth();
	public int getMapHeight();
}
