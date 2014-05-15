package com.cmov.bomberman.model.net;

import android.util.Log;
import com.cmov.bomberman.model.Game;

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
	}};

	private final Game game;
	private final CommunicationChannel commChan;

	public GameConnectionHandler(Game game, CommunicationChannel commChan) throws IOException {
		this.game = game;
		this.commChan = commChan;


	}

	public void run() {
		Log.i(TAG, "Handling a game connection");

		try {
			while (true) {
				ObjectOutputStream out = commChan.getOut();
				ObjectInputStream in = commChan.getIn();
				String commandType = in.readUTF();

				Log.i(TAG, "Received command: " + commandType);

				GameCommand command = commandList.get(commandType);
				command.execute(game, in, out);
			}
		}
		catch (SocketException e) {
			// Socket was closed
			e.printStackTrace();
		}
		catch (EOFException e) {
			// Stream was closed
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
