package com.cmov.bomberman.model.net;

import android.util.Log;
import com.cmov.bomberman.model.Player;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;

public class PlayerConnectionHandler implements Runnable {
	private final String TAG = getClass().getSimpleName();

	private final Map<String, PlayerCommand> commandList = new HashMap<String, PlayerCommand>() {{
		put("update", new UpdateCommand());
		put("onGameStart", new OnGameStartCommand());
		put("onGameEnd", new OnGameEndCommand());
		put("getNextActionName", new GetNextActionNameCommand());
		put("handleEvent", new HandleEventCommand());
	}};

	private final String username;
	private final Player player;
	private final CommunicationChannel commChan;

	public PlayerConnectionHandler(String username, Player player, CommunicationChannel commChan) {
		this.username = username;
		this.player = player;
		this.commChan = commChan;
	}

	public void run() {
		Log.i(TAG, "Handling a player connection");

		try {
			while (true) {
				ObjectOutputStream out = commChan.getOut();
				ObjectInputStream in = commChan.getIn();
				String commandType = in.readUTF();

				Log.v(TAG, "Received command: " + commandType);

				PlayerCommand command = commandList.get(commandType);
				command.execute(username, player, in, out);
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
