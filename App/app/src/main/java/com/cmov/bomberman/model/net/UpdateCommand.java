package com.cmov.bomberman.model.net;

import com.cmov.bomberman.model.Player;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;

public class UpdateCommand implements PlayerCommand {
	@Override
	public void execute(final Player player, final InputStream in, final OutputStream out) {
		try {
			ObjectInputStream objectIn = new ObjectInputStream(in);
			String msg = objectIn.readUTF();
			player.update(msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
