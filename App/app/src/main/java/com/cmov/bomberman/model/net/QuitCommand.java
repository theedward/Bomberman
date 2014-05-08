package com.cmov.bomberman.model.net;

import com.cmov.bomberman.model.Game;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;

public class QuitCommand implements GameCommand {
	@Override
	public void execute(final Game game, final InputStream in, final OutputStream out) {
		try {
			ObjectInputStream objIn = new ObjectInputStream(in);
			String username = objIn.readUTF();
			game.quit(username);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
