package com.cmov.bomberman.model.net;

import com.cmov.bomberman.model.Player;
import com.cmov.bomberman.model.Position;
import com.cmov.bomberman.model.agent.Algorithm;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * The only thing this class does is send all the requests through the output stream.
 */
public class PlayerProxy implements Player {
	private ControllableProxy controllerProxy;
	private ObjectInputStream in;
	private ObjectOutputStream out;

	public PlayerProxy(final ObjectInputStream in, final ObjectOutputStream out) {
		this.in = in;
		this.out = out;
		this.controllerProxy = new ControllableProxy(in, out);
	}

	@Override
	public void update(final String msg) {
		try {
			out.writeUTF("update");
			out.writeUTF(msg);
			out.flush();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onGameStart(final int level, List<Position> wallPositions) {
		try {
			out.writeUTF("onGameStart");
			out.writeInt(level);
			out.writeObject(new LinkedList<Position>(wallPositions));
			out.flush();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onGameEnd(final Map<String, Integer> scores) {
		try {
			out.writeUTF("onGameEnd");
			out.writeObject(new TreeMap<String, Integer>(scores));
			out.flush();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Algorithm getController() {
		return controllerProxy;
	}
}
