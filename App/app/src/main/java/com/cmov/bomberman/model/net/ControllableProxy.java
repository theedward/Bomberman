package com.cmov.bomberman.model.net;

import com.cmov.bomberman.model.Event;
import com.cmov.bomberman.model.agent.Algorithm;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ControllableProxy implements Algorithm {
	private final String TAG = getClass().getSimpleName();

	private ObjectInputStream in;
	private ObjectOutputStream out;
	private String nextActionName;

	public ControllableProxy(final ObjectInputStream in, final ObjectOutputStream out) {
		this.in = in;
		this.out = out;
	}

	public synchronized void setNextActionName(String action) {
		nextActionName = action;
		this.notify();
	}

	@Override
	public synchronized String getNextActionName() {
		try {
			out.writeUTF("getNextActionName");
			out.flush();

			// Waits for the setNextActionName
			try {
				this.wait();
				return nextActionName;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

	@Override
	public synchronized void handleEvent(final Event event) {
		try {
			out.writeUTF("handleEvent");
			out.writeUTF(event.name());
			out.flush();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
