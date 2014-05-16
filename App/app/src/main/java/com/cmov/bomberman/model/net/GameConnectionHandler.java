package com.cmov.bomberman.model.net;

import android.util.Log;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;

public class GameConnectionHandler implements Runnable {
	private final String TAG = getClass().getSimpleName();

	private final Map<String, GameCommand> commandList = new HashMap<String, GameCommand>() {{
		put("join", new JoinCommand());
		put("pause", new PauseCommand());
		put("unpause", new UnpauseCommand());
		put("quit", new QuitCommand());
		put("setNextActionName", new SetNextActionNameCommand());
	}};

	private final GameServer game;
	private final CommunicationChannel commChan;

	public GameConnectionHandler(GameServer game, CommunicationChannel commChan) {
		this.game = game;
		this.commChan = commChan;
	}

	@Override
	public void run() {
		Log.i(TAG, "Handling a game connection");

		try {
			while (true) {
				ObjectOutputStream out = commChan.getOut();
				ObjectInputStream in = commChan.getIn();
				String commandType = in.readUTF();

				Log.v(TAG, "Received command: " + commandType);

				GameCommand command = commandList.get(commandType);
				if (command != null) {
					command.execute(game, in, out);
				}
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
