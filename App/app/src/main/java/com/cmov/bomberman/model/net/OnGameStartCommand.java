package com.cmov.bomberman.model.net;

import com.cmov.bomberman.model.Player;
import com.cmov.bomberman.model.Position;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.util.List;

public class OnGameStartCommand implements PlayerCommand {
	@Override
	@SuppressWarnings("unchecked")
	public void execute(final Player player, final InputStream in, final OutputStream out) {
		try {
			ObjectInputStream objectIn = new ObjectInputStream(in);
			int level = objectIn.readInt();
			List<Position> wallPositions = (List<Position>) objectIn.readObject();
			player.onGameStart(level, wallPositions);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}
