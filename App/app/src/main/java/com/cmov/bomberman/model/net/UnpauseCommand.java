package com.cmov.bomberman.model.net;

import com.cmov.bomberman.model.Game;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class UnpauseCommand implements GameCommand {
	@Override
	public void execute(final Game game, final ObjectInputStream in, final ObjectOutputStream out) {
		try {
			String username = in.readUTF();
			game.unpause(username);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
