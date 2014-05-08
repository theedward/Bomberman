package com.cmov.bomberman.model.net;

import com.cmov.bomberman.model.Player;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;

public class PlayerConnectionHandler implements Runnable {
	private final Map<String, PlayerCommand> commandList = new HashMap<String, PlayerCommand>() {{
		put("update", new UpdateCommand());
		put("onGameStart", new OnGameStartCommand());
		put("onGameEnd", new OnGameEndCommand());
		put("getNextActionName", new GetNextActionNameCommand());
		put("handleEvent", new HandleEventCommand());
	}};

	private final Player player;
	private final ObjectInputStream in;
	private final ObjectOutputStream out;

	public PlayerConnectionHandler(Player player, ObjectInputStream in, ObjectOutputStream out) {
		this.player = player;
		this.in = in;
		this.out = out;
	}

	public void run() {
		try {
			while (true) {
				String commandType = in.readUTF();
				PlayerCommand command = commandList.get(commandType);
				command.execute(player, in, out);
			}
		}
		catch (SocketException e) {
			// Socket was closed
		}
		catch (EOFException e) {
			// Stream was closed
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
