package com.cmov.bomberman.model.net;

import android.util.Log;
import com.cmov.bomberman.model.Game;
import com.cmov.bomberman.model.GameImpl;
import com.cmov.bomberman.model.Player;
import pt.utl.ist.cmov.wifidirect.sockets.SimWifiP2pSocket;
import pt.utl.ist.cmov.wifidirect.sockets.SimWifiP2pSocketServer;

import java.io.IOException;
import java.net.SocketException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GameServer implements Game {
	private static final int GAME_SERVER_PORT = 10001;

	private final String TAG = getClass().getSimpleName();

	private Game game;
	private ExecutorService executor;
	private SimWifiP2pSocketServer serverSocket;
	private List<CommunicationChannel> commChannels;

	public GameServer(int level) {
		game = new GameImpl(level);
		executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		commChannels = new LinkedList<CommunicationChannel>();

		acceptPlayers();
	}

	private void acceptPlayers() {
		executor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					serverSocket = new SimWifiP2pSocketServer(GAME_SERVER_PORT);
					while (true) {
						final SimWifiP2pSocket clientSocket = serverSocket.accept();
						final CommunicationChannel commChan = new CommunicationChannel(clientSocket);

						commChannels.add(commChan);
						executor.submit(new GameConnectionHandler(GameServer.this, commChan));
					}
				}
				catch (SocketException e) {
					// Socket was closed
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * if I'm not the server I only need to send a message to it with the right protocol
	 * if I am the server I just need to call the function with the right player's username
	 *
	 * @param username the player's username
	 */
	public void pause(final String username) {
		game.pause(username);
	}

	/**
	 * if I'm not the server I only need to send a message to it with the right protocol
	 * if I am the server I just need to call the function with the right player's username
	 *
	 * @param username the player's username
	 */
	public void unpause(final String username) {
		game.unpause(username);
	}

	/**
	 * if I'm not the server I only need to send a message to it with the right protocol
	 * if I am the server I just need to call the function with the right player's username
	 *
	 * @param username the player's username
	 */
	public void quit(final String username) {
		game.quit(username);
	}

	public void join(final String username, Player player) {
		game.join(username, player);
	}

	public void start() {
		game.start();
	}

	public void pause() {
		// Nothing to do here
	}

	public void unpause() {
		// Nothing to do here
	}

	public void onDestroy() {
		// Stop accepting new requests
		try {
			serverSocket.close();
		}
		catch (IOException e) {
			// Socket already closed
		}

		// Close open sockets
		for (CommunicationChannel commChan : commChannels) {
			try {
				commChan.close();
			} catch (IOException e) {
				// Socket already closed
			}
		}

		// Shutdown other threads
		executor.shutdownNow();

		Log.i(TAG, "OnDestroy was successful");
	}
}
