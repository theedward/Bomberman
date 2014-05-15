package com.cmov.bomberman.model.net;

import android.util.Log;
import com.cmov.bomberman.model.Game;
import com.cmov.bomberman.model.Player;
import pt.utl.ist.cmov.wifidirect.sockets.SimWifiP2pSocket;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GameClient implements Game {
	private static final int GAME_SERVER_PORT = 10001;

	private final String TAG = getClass().getSimpleName();

	private SimWifiP2pSocket gameSocket;
	private ObjectInputStream gameInputStream;
	private ObjectOutputStream gameOutputStream;
	private ExecutorService executor;

	private Player localPlayer;

	/**
	 * Client constructor
	 */
	public GameClient(String hostname) {
		executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

		// read server host
		while (true) {
			try {
				// connect to the server
				gameSocket = new SimWifiP2pSocket(hostname, GAME_SERVER_PORT);
				Log.i(TAG, "Successfully joined the game server");
				break;
			}
			catch (IOException e) {
				// Server is still not available
				Log.e(TAG, "Failed to join the game server");
				Log.e(TAG, e.getMessage());
			}
		}
	}

	private void handleGameRequests(String username, final SimWifiP2pSocket gameSocket) throws IOException {
		ObjectOutputStream out = new ObjectOutputStream(gameSocket.getOutputStream());

		// Send identification to server
		identifyToServer(username, out);

		// Set streams for later use
		this.gameInputStream = new ObjectInputStream(gameSocket.getInputStream());
		this.gameOutputStream = out;

		executor.submit(new PlayerConnectionHandler(localPlayer, this.gameInputStream, this.gameOutputStream));
	}

	private void identifyToServer(String username, ObjectOutputStream out) throws IOException {
		out.writeUTF(username);
		out.flush();
	}

	public void pause(final String username) {
		try {
			gameOutputStream.writeUTF("pause");
			gameOutputStream.writeUTF(username);
			gameOutputStream.flush();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void unpause(final String username) {
		try {
			gameOutputStream.writeUTF("unpause");
			gameOutputStream.writeUTF(username);
			gameOutputStream.flush();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void quit(final String username) {
		try {
			gameOutputStream.writeUTF("quit");
			gameOutputStream.writeUTF(username);
			gameOutputStream.flush();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void join(final String username, final Player player) {
		localPlayer = player;

		// handle game requests
		try {
			handleGameRequests(username, this.gameSocket);
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		try {
			gameOutputStream.writeUTF("join");
			gameOutputStream.writeUTF(username);
			gameOutputStream.flush();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void start() {
		// Nothing to do here
	}

	public void pause() {
		// Nothing to do here
	}

	public void unpause() {
		// Nothing to do here
	}

	public void onDestroy() {
		try {
			gameInputStream.close();
			gameOutputStream.close();
		}
		catch (IOException e) {
			// Stream already closed
		}

		// Shutdown other threads
		executor.shutdownNow();

		System.out.println("GameClient#onDestroy() was successful");
	}
}
