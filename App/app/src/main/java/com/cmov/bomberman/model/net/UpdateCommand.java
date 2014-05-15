package com.cmov.bomberman.model.net;

import com.cmov.bomberman.model.Player;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class UpdateCommand implements PlayerCommand {
	@Override
	public void execute(final String username, final Player player, final ObjectInputStream in, final ObjectOutputStream out) {
		try {
			String msg = in.readUTF();
			player.update(msg);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
