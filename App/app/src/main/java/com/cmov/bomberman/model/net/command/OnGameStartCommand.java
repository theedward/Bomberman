package com.cmov.bomberman.model.net.command;

import com.cmov.bomberman.model.Player;
import com.cmov.bomberman.model.Position;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedList;

public class OnGameStartCommand implements PlayerCommand {
	@Override
	@SuppressWarnings("unchecked")
	public void execute(final String username, final Player player, final ObjectInputStream in, final ObjectOutputStream out) {
		try {
			int level = in.readInt();
			LinkedList<Position> wallPositions = (LinkedList<Position>) in.readObject();
			player.onGameStart(level, wallPositions);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}
