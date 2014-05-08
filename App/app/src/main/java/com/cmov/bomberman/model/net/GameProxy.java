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

public class GameProxy implements Game, Runnable {
	private static final int GAME_SERVER_PORT = 8888;

	private final Map<String, ObjectInputStream> clientInputStream = new TreeMap<String, ObjectInputStream>();
	private final Map<String, ObjectOutputStream> clientOutputStream = new TreeMap<String, ObjectOutputStream>();

	private Game game;
	private Player localPlayer;

	private int level;
	private ExecutorService executor;
	private ServerSocket serverSocket;
	private ObjectInputStream gameInputStream;
	private ObjectOutputStream gameOutputStream;
	private boolean isServer;

	/**
	 * Server constructor
	 **/
	public GameProxy(int level) {
		isServer = true;
		this.level = level;
		game = new GameImpl(level);
		executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		executor.submit(this);
	}

	/**
	 * Client constructor
	 */
	public GameProxy(String hostname, String username) {
		isServer = false;
		executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

		// read server host
		try {
			// connect to the server
			Socket gameSocket = new Socket(hostname, GAME_SERVER_PORT);

			// handle game requests
			handleGameRequests(username, gameSocket);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}


	public void onDestroy() {
		// Stop accepting new requests
		if (isServer) {
			try {
				serverSocket.close();
			}
			catch (IOException e) {
				// Socket already closed
			}

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
		} else {
			try {
				gameInputStream.close();
				gameOutputStream.close();
			}
			catch (IOException e) {
				// Stream already closed
			}
		}

		// Shutdown other threads
		executor.shutdownNow();

		System.out.println("GameProxy#onDestroy() was successful");
	}

	/**
	 *
	 */
	private void acceptPlayers() {
		executor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					serverSocket = new ServerSocket(GAME_SERVER_PORT);
					while (true) {
						final Socket clientSocket = serverSocket.accept();
						System.out.println("A user appeared");
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

		executor.submit(new GameConnectionHandler(GameProxy.this, in, out));
	}

	private void handleGameRequests(String username, final Socket gameSocket) throws IOException {
		ObjectOutputStream out = new ObjectOutputStream(gameSocket.getOutputStream());

		// Send identification to server
		identifyToServer(username, out);

		// Set streams for later use
		this.gameInputStream = new ObjectInputStream(gameSocket.getInputStream());
		this.gameOutputStream = out;

		executor.submit(new PlayerConnectionHandler(localPlayer, this.gameInputStream, this.gameOutputStream));
	}

	private String parseIdentification(ObjectInputStream in) throws IOException {
		return in.readUTF();
	}

	private void identifyToServer(String username, ObjectOutputStream out) throws IOException {
		out.writeUTF(username);
		out.flush();
	}

	/**
	 * if I'm not the server I only need to send a message to it with the right protocol
	 * if I am the server I just need to call the function with the right player's username
	 * @param username the player's username
	 */
    public void pause(final String username) {
        if (isServer) {
			game.pause(username);
		} else {
            try {
				gameOutputStream.writeUTF("pause");
				gameOutputStream.writeUTF(username);
				gameOutputStream.flush();
			} catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

	/**
	 * if I'm not the server I only need to send a message to it with the right protocol
	 * if I am the server I just need to call the function with the right player's username
	 * @param username the player's username
	 */
	public void unpause(final String username) {
		if (isServer) {
			game.unpause(username);
		} else {
			try {
				gameOutputStream.writeUTF("unpause");
				gameOutputStream.writeUTF(username);
				gameOutputStream.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * if I'm not the server I only need to send a message to it with the right protocol
	 * if I am the server I just need to call the function with the right player's username
	 * @param username the player's username
	 */
	public void quit(final String username) {
		if (isServer) {
			game.quit(username);
		} else {
			try {
				gameOutputStream.writeUTF("quit");
				gameOutputStream.writeUTF(username);
				gameOutputStream.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void join(final String username, Player player) {
		localPlayer = player;

		if (isServer) {
			game.join(username, localPlayer);
		} else {
			try {
				gameOutputStream.writeUTF("join");
				gameOutputStream.writeUTF(username);
				gameOutputStream.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void start() {
		if (isServer) {
			game.start();
		}
	}

	public void pause() {
		// Nothing to do here
	}

	public void unpause() {
		// Nothing to do here
	}

	@Override
	public void run() {
		acceptPlayers();
	}
}
