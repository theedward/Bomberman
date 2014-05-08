package com.cmov.bomberman.model.net;

import com.cmov.bomberman.model.Player;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.util.Map;

public class OnGameEndCommand implements PlayerCommand {
	@Override
	@SuppressWarnings("unchecked")
	public void execute(final Player player, final InputStream in, final OutputStream out) {
		try {
			ObjectInputStream objectIn = new ObjectInputStream(in);
			Map<String, Integer> scores = (Map<String, Integer>) objectIn.readObject();
			player.onGameEnd(scores);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}
