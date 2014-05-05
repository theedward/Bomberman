package com.cmov.bomberman.model;

import com.cmov.bomberman.model.agent.Algorithm;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.Map;

/**
 * The only thing this class does is send all the requests through the output stream.
 */
public class PlayerProxy implements Player {
	private ControllableProxy controllerProxy;
	private Socket clientSocket;

	public PlayerProxy(final Socket clientSocket) {
		this.clientSocket = clientSocket;
		this.controllerProxy = new ControllableProxy(clientSocket);
	}

	public void setClientSocket(final Socket clientSocket) {
		this.clientSocket = clientSocket;
		controllerProxy.setClientSocket(clientSocket);
	}

	@Override
	public void update(final String msg) {
		try {
			DataOutputStream out = new DataOutputStream(new BufferedOutputStream(clientSocket.getOutputStream()));
			out.writeUTF("UPDATE#" + msg);
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onGameStart(final List<Position> wallPositions) {
		try {
			ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(clientSocket.getOutputStream()));
			out.writeUTF("ONGAMESTART#");
			out.writeObject(wallPositions);
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onGameEnd(final Map<String, Integer> scores) {
		try {
			ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(clientSocket.getOutputStream()));
			out.writeUTF("ONGAMEEND#");
			out.writeObject(scores);
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Algorithm getController() {
		return controllerProxy;
	}

	@Override
	public void setAgentId(final int id) {
		try {
			DataOutputStream out = new DataOutputStream(new BufferedOutputStream(clientSocket.getOutputStream()));
			out.writeUTF("SETAGENTID#");
			out.writeInt(id);
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
