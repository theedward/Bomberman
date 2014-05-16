package com.cmov.bomberman.model.net.command;

import android.util.Log;
import com.cmov.bomberman.model.net.GameServer;
import com.cmov.bomberman.model.net.PlayerProxy;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class JoinCommand implements GameCommand {
	private final String TAG = getClass().getSimpleName();

	@Override
	public void execute(final GameServer game, final ObjectInputStream in, final ObjectOutputStream out) {
		try {
			String username = in.readUTF();
			Log.i(TAG, username);

			PlayerProxy proxy = new PlayerProxy(in, out);
			game.join(username, proxy);
			game.addProxy(username, proxy);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
