package com.cmov.bomberman.model.net.command;

import com.cmov.bomberman.model.Player;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;

public class OnGameEndCommand implements PlayerCommand {
	@Override
	@SuppressWarnings("unchecked")
	public void execute(final String username, final Player player, final ObjectInputStream in,
						final ObjectOutputStream out) {
		try {
			Map<String, Integer> scores = (Map<String, Integer>) in.readObject();
			player.onGameEnd(scores);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}
