package com.cmov.bomberman.model.net.command;

import com.cmov.bomberman.model.Player;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class GetNextActionNameCommand implements PlayerCommand {
	@Override
	public void execute(final String username, final Player player, final ObjectInputStream in,
						final ObjectOutputStream out) {
		try {
			out.writeUTF("setNextActionName");
			out.writeUTF(username);
			out.writeUTF(player.getController().getNextActionName());
			out.flush();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
