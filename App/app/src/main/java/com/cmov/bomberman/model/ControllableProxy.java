package com.cmov.bomberman.model;

import com.cmov.bomberman.model.agent.Algorithm;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ControllableProxy implements Algorithm {
	private Socket clientSocket;

	public ControllableProxy(final Socket clientSocket) {
		this.clientSocket = clientSocket;
	}

	public void setClientSocket(final Socket clientSocket) {
		this.clientSocket = clientSocket;
	}

	@Override
	public String getNextActionName() {
		try {
			DataOutputStream out = new DataOutputStream(new BufferedOutputStream(clientSocket.getOutputStream()));
			out.writeUTF("GETNEXTACTIONNAME#");
			out.flush();

			DataInputStream in = new DataInputStream(clientSocket.getInputStream());
			return in.readUTF();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

	@Override
	public void handleEvent(final Event event) {
		try {
			DataOutputStream out = new DataOutputStream(new BufferedOutputStream(clientSocket.getOutputStream()));
			out.writeUTF("HANDLEEVENT#");
			out.writeUTF(event.toString());
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
