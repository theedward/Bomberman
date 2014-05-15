package com.cmov.bomberman.model.net;

import pt.utl.ist.cmov.wifidirect.sockets.SimWifiP2pSocket;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Always get output stream before getting input stream.
 */
public class CommunicationChannel {
	private SimWifiP2pSocket socket;
	private ObjectInputStream in;
	private ObjectOutputStream out;

	public CommunicationChannel(final SimWifiP2pSocket socket) throws IOException {
		this.socket = socket;
		out = new ObjectOutputStream(socket.getOutputStream());
		out.flush();
		in = new ObjectInputStream(socket.getInputStream());
	}

	public ObjectInputStream getIn() throws IOException {
		return in;
	}

	public ObjectOutputStream getOut() throws IOException {
		return out;
	}

	public void close() throws IOException {
		socket.close();
	}
}
