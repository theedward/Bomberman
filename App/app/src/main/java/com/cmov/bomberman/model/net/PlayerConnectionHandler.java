package com.cmov.bomberman.model.net;

import com.cmov.bomberman.model.Player;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
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
	private final Socket socket;

	public PlayerConnectionHandler(Player player, Socket socket) {
		this.player = player;
		this.socket = socket;
	}

	public void run() {
		try {
			while (true) {
				ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
				String commandType = in.readUTF();
				PlayerCommand command = commandList.get(commandType);
				command.execute(player, socket.getInputStream(), socket.getOutputStream());
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
