package com.cmov.bomberman.model.net;

import com.cmov.bomberman.model.Game;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.SocketException;
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
	private final ObjectInputStream in;
	private final ObjectOutputStream out;

	public GameConnectionHandler(Game game, ObjectInputStream in, ObjectOutputStream out) {
		this.game = game;
		this.in = in;
		this.out = out;
	}

	public void run() {
		try {
			while (true) {
				String commandType = in.readUTF();
				GameCommand command = commandList.get(commandType);
				command.execute(game, in, out);
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
