package com.cmov.bomberman.model.net.command;

import com.cmov.bomberman.model.net.GameServer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SetNextActionNameCommand implements GameCommand {
	@Override
	public void execute(final GameServer game, final ObjectInputStream in, final ObjectOutputStream out) {
		try {
			String username = in.readUTF();
			String action = in.readUTF();
			game.setNextActionName(username, action);
		} catch (IOException e){
			e.printStackTrace();
		}
	}
}
