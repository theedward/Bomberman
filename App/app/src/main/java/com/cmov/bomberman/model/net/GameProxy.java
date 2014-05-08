package com.cmov.bomberman.model.net;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import com.cmov.bomberman.model.Game;
import com.cmov.bomberman.model.GameImpl;
import com.cmov.bomberman.model.Player;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GameProxy extends Service implements Game {
	private static final int GAME_SERVER_PORT = 8888;

	private final String TAG = this.getClass().getSimpleName();

    private final IBinder mBinder = new Binder() {
		public GameProxy getService() {
			return GameProxy.this;
		}
	};

	private final Map<String, Socket> clientSockets = new TreeMap<String, Socket>();

	private GameImpl game;
	private Player localPlayer;

    private int level;
	private ExecutorService executor;
	private ServerSocket serverSocket;
	private Socket gameSocket;
    private boolean isServer;

	/**
	 * When the gameProxy starts, it will start by verifying if it has the role of server or of client.
	 * If it's the server, it will start waiting for clients on the port GAME_SERVER_PORT. When a client connects,
	 * it'll wait for his username and then it adds the pair <username, clientSocket> to the saved clients.
	 *
	 * If it's a client, it will connect to the server and send it's username.
	 *
	 * The list of variables used that are received from the intent are:
	 * 	boolean isServer: specifies if this gameProxy has the role of the server;
	 * 	int level: specifies the level to be played. Only needed when GameProxy has the server role;
	 * 	hostname: specifies the GameProxy's server address. Only needed when isServer is false;
	 * 	username: Only needed when isServer is false.
	 */
    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        Bundle extras = intent.getExtras();
        if (extras != null) {
			this.isServer = extras.getBoolean("isServer");
			if (isServer) {
				this.level = extras.getInt("level");
				acceptPlayers();
			} else {
				// read server host
				String hostname = extras.getString("hostname");
				try {
					// connect to the server
					Socket gameSocket = new Socket(hostname, GAME_SERVER_PORT);

					// Send the username
					String username = extras.getString("username");
					identifyToServer(username, gameSocket);

					// handle game requests
					handleGameRequests(gameSocket);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
        }

		game = new GameImpl(level);
		executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		Log.i(TAG, "onStartCommand went fine.");

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

	@Override
	public void onDestroy() {
		super.onDestroy();

		game = null;
		executor.shutdownNow();
	}

	/**
	 *
	 */
	private void acceptPlayers() {
		executor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					while (true) {
						serverSocket = new ServerSocket(GAME_SERVER_PORT);
						Socket clientSocket = serverSocket.accept();
						handlePlayerRequests(clientSocket);
					}
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}

	private void handlePlayerRequests(Socket clientSocket) {
		// Get the username
		String username = parseIdentification(clientSocket);
		clientSockets.put(username, clientSocket);

		executor.submit(new GameConnectionHandler(GameProxy.this, clientSocket));
	}

	private void handleGameRequests(Socket gameSocket) {
		this.gameSocket = gameSocket;
		executor.submit(new PlayerConnectionHandler(localPlayer, gameSocket));
	}

	private String parseIdentification(Socket socket) {
		try {
			ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
			return in.readUTF();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

	private void identifyToServer(String username, Socket socket) {
		try {
			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
			out.writeUTF(username);
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * if I'm not the server I only need to send a message to it with the right protocol
	 * if I am the server I just need to call the function with the right player's username
	 * @param username the player's username
	 */
    @Override
    public void pause(final String username) {
        if (isServer) {
			game.pause(username);
		} else {
            try {
                ObjectOutputStream objOut = new ObjectOutputStream(gameSocket.getOutputStream());
                objOut.writeUTF("pause");
				objOut.writeUTF(username);
                objOut.flush();
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
	@Override
	public void unpause(final String username) {
		if (isServer) {
			game.pause(username);
		} else {
			try {
				ObjectOutputStream objOut = new ObjectOutputStream(gameSocket.getOutputStream());
				objOut.writeUTF("unpause");
				objOut.writeUTF(username);
				objOut.flush();
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
	@Override
	public void quit(final String username) {
		if (isServer) {
			game.pause(username);
		} else {
			try {
				ObjectOutputStream objOut = new ObjectOutputStream(gameSocket.getOutputStream());
				objOut.writeUTF("quit");
				objOut.writeUTF(username);
				objOut.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

    @Override
    public void join(final String username, Player player) {
		localPlayer = player;

		if (isServer) {
			game.join(username, localPlayer);
        } else {
			try {
				ObjectOutputStream objOut = new ObjectOutputStream(gameSocket.getOutputStream());
				objOut.writeUTF("join");
				objOut.writeUTF(username);
				objOut.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
    }

	@Override
	public void start() {
		// only the server owner can start the game
		if (isServer) {
			game.start();
		}
	}

	@Override
	public void pause() {
		// Nobody can't pause the game. It will run until it has finished.
	}

	@Override
	public void unpause() {
		// Nobody can't unpause the game. It will run until it has finished.
	}
}
