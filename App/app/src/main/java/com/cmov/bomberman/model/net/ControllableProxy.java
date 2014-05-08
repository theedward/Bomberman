package com.cmov.bomberman.model.net;

import com.cmov.bomberman.model.Event;
import com.cmov.bomberman.model.agent.Algorithm;

import java.io.*;

public class ControllableProxy implements Algorithm {
	private InputStream in;
	private OutputStream out;

	public ControllableProxy(final InputStream in, final OutputStream out) {
		this.in = in;
		this.out = out;
	}

	@Override
	public String getNextActionName() {
		try {
			ObjectOutputStream objOut = new ObjectOutputStream(out);
			objOut.writeUTF("getNextActionName");
			objOut.flush();

			ObjectInputStream objIn = new ObjectInputStream(in);
			return objIn.readUTF();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

	@Override
	public void handleEvent(final Event event) {
		try {
			ObjectOutputStream objOut = new ObjectOutputStream(out);
			objOut.writeUTF("handleEvent");
			objOut.writeUTF(event.name());
			objOut.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
