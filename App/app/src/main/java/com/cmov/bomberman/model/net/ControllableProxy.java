package com.cmov.bomberman.model.net;

import com.cmov.bomberman.model.Event;
import com.cmov.bomberman.model.agent.Algorithm;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ControllableProxy implements Algorithm {
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private String nextActionName;

	public ControllableProxy(final ObjectInputStream in, final ObjectOutputStream out) {
		this.in = in;
		this.out = out;
	}

	public void setNextActionName(String action) {
		nextActionName = action;

		synchronized (this) {
			this.notify();
		}
	}

	@Override
	public String getNextActionName() {
		try {
			out.writeUTF("getNextActionName");
			out.flush();

			// Waits for the setNextActionName
			synchronized (this) {
				try {
					this.wait();
					return nextActionName;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

	@Override
	public void handleEvent(final Event event) {
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
