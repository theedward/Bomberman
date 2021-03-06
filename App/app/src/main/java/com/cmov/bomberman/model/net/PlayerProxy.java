package com.cmov.bomberman.model.net;

import android.util.Log;
import com.cmov.bomberman.model.Player;
import com.cmov.bomberman.model.Position;
import com.cmov.bomberman.model.agent.Algorithm;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * The only thing this class does is send all the requests through the output stream.
 */
public class PlayerProxy implements Player {
	private final String TAG = getClass().getSimpleName();

	private ControllableProxy controllerProxy;
	private ObjectOutputStream out;

	public PlayerProxy(final ObjectOutputStream out) {
		this.out = out;
		this.controllerProxy = new ControllableProxy(out);
	}

	public void setNextActionName(String action) {
		controllerProxy.setNextActionName(action);
	}

	@Override
	public synchronized void update(final String msg) {
		try {
			out.writeUTF("update");
			out.writeUTF(msg);
			out.flush();
		}
		catch (IOException e) {
			Log.e(TAG, "Error sending update to client");
		}
	}

	@Override
	public synchronized void onGameStart(final int level, List<Position> wallPositions) {
		try {
			out.writeUTF("onGameStart");
			out.writeInt(level);
			out.writeObject(new LinkedList<Position>(wallPositions));
			out.flush();
		}
		catch (IOException e) {
			Log.e(TAG, "Error sending onGameStart to client");
		}
	}

	@Override
	public synchronized void onGameEnd(final Map<String, Integer> scores) {
		try {
			out.writeUTF("onGameEnd");
			out.writeObject(new TreeMap<String, Integer>(scores));
			out.flush();
		}
		catch (IOException e) {
			Log.e(TAG, "Error sending onGameEnd to client");
		}
	}

	@Override
	public synchronized Algorithm getController() {
		return controllerProxy;
	}

	/**
	 * Unlock all possible locks
	 */
	public synchronized void onDestroy() {
		notifyAll();
	}
}
