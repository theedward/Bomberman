package com.cmov.bomberman.model.net;

import com.cmov.bomberman.model.Game;
import com.cmov.bomberman.model.GameImpl;
import com.cmov.bomberman.model.Player;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GameServer implements Game {
	private static final int GAME_SERVER_PORT = 8888;

	private final Map<String, ObjectInputStream> clientInputStream = new TreeMap<String, ObjectInputStream>();
	private final Map<String, ObjectOutputStream> clientOutputStream = new TreeMap<String, ObjectOutputStream>();

	private Game game;
	private ExecutorService executor;
	private ServerSocket serverSocket;

	public GameServer(int level) {
		game = new GameImpl(level);
		executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		executor.submit(new Runnable() {
			@Override
			public void run() {
				acceptPlayers();
			}
		});
	}

	private void acceptPlayers() {
		executor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					serverSocket = new ServerSocket(GAME_SERVER_PORT);
					while (true) {
						final Socket clientSocket = serverSocket.accept();
						handlePlayerRequests(clientSocket);
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

	private void handlePlayerRequests(final Socket clientSocket) throws IOException {
		ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());

		// Get the username
		String username = parseIdentification(in);

		ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
		clientInputStream.put(username, in);
		clientOutputStream.put(username, out);

		executor.submit(new GameConnectionHandler(GameServer.this, in, out));
	}

	private String parseIdentification(ObjectInputStream in) throws IOException {
		return in.readUTF();
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

		// Close current open streams
		for (ObjectInputStream in : clientInputStream.values()) {
			try {
				in.close();
			}
			catch (IOException e) {
				// Stream already closed
			}
		}

		for (ObjectOutputStream out : clientOutputStream.values()) {
			try {
				out.close();
			}
			catch (IOException e) {
				// Stream already closed
			}
		}

		// Shutdown other threads
		executor.shutdownNow();

		System.out.println("GameProxy#onDestroy() was successful");
	}
}
