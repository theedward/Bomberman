package com.cmov.bomberman.model.net;

import com.cmov.bomberman.model.Game;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class GameConnectionHandler implements Runnable {
	private final Map<String, GameCommand> commandList = new HashMap<String, GameCommand>() {{
		put("join", new JoinCommand());
		put("pause", new PauseCommand());
		put("unpause", new UnpauseCommand());
		put("quit", new QuitCommand());
	}};

	private final Game game;
	private final Socket socket;

	public GameConnectionHandler(Game game, Socket socket) {
		this.game = game;
		this.socket = socket;
	}

	public void run() {
		try {
			while (true) {
				ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
				String commandType = in.readUTF();
				GameCommand command = commandList.get(commandType);
				command.execute(game, socket.getInputStream(), socket.getOutputStream());
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
