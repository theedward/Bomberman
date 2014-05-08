package com.cmov.bomberman.model.net;

import com.cmov.bomberman.model.Player;
import com.cmov.bomberman.model.Position;
import com.cmov.bomberman.model.agent.Algorithm;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

/**
 * The only thing this class does is send all the requests through the output stream.
 */
public class PlayerProxy implements Player {
	private ControllableProxy controllerProxy;
	private InputStream in;
	private OutputStream out;

	public PlayerProxy(final InputStream in, final OutputStream out) {
		this.in = in;
		this.out = out;
		this.controllerProxy = new ControllableProxy(in, out);
	}

	@Override
	public void update(final String msg) {
		try {
			ObjectOutputStream objOut = new ObjectOutputStream(out);
			objOut.writeUTF("update");
			objOut.writeUTF(msg);
			objOut.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onGameStart(final int level, final List<Position> wallPositions) {
		try {
			ObjectOutputStream objOut = new ObjectOutputStream(out);
			objOut.writeUTF("onGameStart#");
			objOut.writeInt(level);
			objOut.writeObject(wallPositions);
			objOut.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onGameEnd(final Map<String, Integer> scores) {
		try {
			ObjectOutputStream objOut = new ObjectOutputStream(out);
			objOut.writeUTF("onGameEnd#");
			objOut.writeObject(scores);
			objOut.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Algorithm getController() {
		return controllerProxy;
	}
}
