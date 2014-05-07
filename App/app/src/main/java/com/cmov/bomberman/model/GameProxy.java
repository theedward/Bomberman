package com.cmov.bomberman.model;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
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
	private Socket server;
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
				try {
					serverSocket = new ServerSocket(GAME_SERVER_PORT);
					startAccepting();
					startReplying();
				} catch (IOException e) {
					Log.e(TAG, e.toString());
				}
			} else {
				// read server host
				String hostname = extras.getString("hostname");
				try {
					// connect to the server and send the username
					this.server = new Socket(hostname, GAME_SERVER_PORT);
					String username = extras.getString("username");
					identifyToServer(username);
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

	private void startAccepting() {
		executor.execute(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						// Waits for a new client.. When one arrived, get his username and add it to the map
						Socket clientSocket = serverSocket.accept();
						DataInputStream in = new DataInputStream(clientSocket.getInputStream());
						String username = in.readUTF();
						synchronized (clientSockets) {
							clientSockets.put(username, clientSocket);
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});
	}

	private void identifyToServer(String username) {
		try {
			DataOutputStream out = new DataOutputStream(server.getOutputStream());
			out.writeUTF(username);
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void startReplying() {
		executor.execute(new Runnable() {
			@Override
			public void run() {
				while (true) {
					readFromSockets();

					// Be gentle with the other threads
					Thread.yield();
				}
			}
		});
	}

	//This function should be used in the main while loop, reading from clientSockets
	public void readFromSockets() {
		byte[] buffer = new byte[1500];
		try {
			synchronized (clientSockets) {
				for (Socket clientSocket : clientSockets.values()) {
					InputStream is = clientSocket.getInputStream();
					if (is.available() > 0) {
						int length = is.read(buffer);
						analyzeMessage(buffer, length);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

    //This function will analyse the message the socket has and call the functions
    // necessary to perform the given task
    public void analyzeMessage(byte[] message, int length) {
		int firstPartEnd = 0;
		while (firstPartEnd < length && (char)message[firstPartEnd] != '#') {
			firstPartEnd++;
		}

		String msgType = new String(message, 0, firstPartEnd);
		Log.i(TAG, "Message type: " + msgType);

		byte[] rest = Arrays.copyOfRange(message, firstPartEnd+1, length);

		// GameProxy message protocol
		String username = handleUsername(rest);
        if (msgType.equals("PAUSE")) {
            pause(username);
        } else if (msgType.equals("UNPAUSE")) {
            unpause(username);
        } else if (msgType.equals("QUIT")) {
            quit(username);
        } else if (msgType.equals("JOIN")) {
            join(username, null);
        }else if (msgType.equals("GETPLAYERUSERNAMES")) {
			Collection<String> usernames = getPlayerUsernames();
			replyGetPlayerUsernames(username, usernames);
		}

		// PlayerProxy message protocol
		if (msgType.equals("UPDATE")) {
			handleUpdate(rest);
		} else if (msgType.equals("ONGAMESTART")) {
			handleOnGameStart(rest);
		} else if (msgType.equals("ONGAMEEND")) {
			handleOnGameEnd(rest);
		} else if (msgType.equals("SETAGENTID")) {
			handleSetAgentId(rest);
		}

		// ControllableProxy message protocol
		if (msgType.equals("GETNEXTACTIONNAME")) {
			String nextActionName = handleGetNextActionName();
			replyGetNextActionName(nextActionName);
		} else if (msgType.equals("HANDLEEVENT")) {
			handleHandleEvent(rest);
		}
    }

	private String handleUsername(byte[] rest) {
		try {
			DataInputStream in = new DataInputStream(new ByteArrayInputStream(rest));
			return in.readUTF();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

	private void handleUpdate(byte[] rest) {
		if (!isServer) {
			try {
				DataInputStream in = new DataInputStream(new ByteArrayInputStream(rest));
				String msg = in.readUTF();
				localPlayer.update(msg);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			Log.e(TAG, "Server owner can't receive updates!");
		}
	}

	@SuppressWarnings("unchecked")
	private void handleOnGameStart(byte[] rest) {
		if (!isServer) {
			try {
				ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(rest));
				List<Position> wallPositions = (List<Position>) in.readObject();
				localPlayer.onGameStart(level, wallPositions);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		} else {
			Log.e(TAG, "Server owner can't receive onGameStart");
		}
	}

	@SuppressWarnings("unchecked")
	private void handleOnGameEnd(byte[] rest) {
		if (!isServer) {
			try {
				ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(rest));
				Map<String, Integer> scores = (Map<String, Integer>) in.readObject();
				localPlayer.onGameEnd(scores);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		} else {
			Log.e(TAG, "Server owner can't receive onGameEnd");
		}
	}

	private void handleSetAgentId(byte[] rest) {
		if (!isServer) {
			try {
				DataInputStream in = new DataInputStream(new ByteArrayInputStream(rest));
				int id = in.readInt();
				localPlayer.setAgentId(id);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			Log.e(TAG, "Server owner can't receive setAgentId");
		}
	}

	private String handleGetNextActionName() {
		return localPlayer.getController().getNextActionName();
	}

	private void replyGetNextActionName(String nextActionName) {
		try {
			DataOutputStream out = new DataOutputStream(server.getOutputStream());
			out.writeUTF(nextActionName);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void handleHandleEvent(byte[] rest) {
		if (!isServer) {
			try {
				DataInputStream in = new DataInputStream(new ByteArrayInputStream(rest));
				Event e = Event.valueOf(in.readUTF());
				localPlayer.getController().handleEvent(e);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			Log.e(TAG, "Server owner can't receive handleEvent");
		}
	}

	private void replyGetPlayerUsernames(String username, Collection<String> usernames) {
		try {
			DataOutputStream out = new DataOutputStream(clientSockets.get(username).getOutputStream());
			for (String s : usernames) {
				out.writeUTF(s);
			}
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
                DataOutputStream writeToServer = new DataOutputStream(server.getOutputStream());
                writeToServer.writeUTF("PAUSE#" + username);
                writeToServer.flush();
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
			game.unpause(username);
		} else {
			try {
				DataOutputStream writeToServer = new DataOutputStream(server.getOutputStream());
				writeToServer.writeUTF("UNPAUSE#" + username);
				writeToServer.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
    }

    @Override
    public void quit(final String username) {
		if (isServer) {
			game.quit(username);
		} else {
			try {
				DataOutputStream writeToServer = new DataOutputStream(server.getOutputStream());
				writeToServer.writeUTF("QUIT#" + username);
				writeToServer.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
    }

    @Override
    public void join(final String username, Player player) {
        if (isServer) {
			// if the player is the local player
			if (player != null) {
				localPlayer = player;
				game.join(username, localPlayer);
			} else {
				// create a player proxy
				Player playerProxy = new PlayerProxy(clientSockets.get(username));
				game.join(username, playerProxy);
			}
        } else {
			try {
				DataOutputStream writeToServer = new DataOutputStream(server.getOutputStream());
				writeToServer.writeUTF("JOIN#" + username);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
    }

	@Override
	public Collection<String> getPlayerUsernames() {
		if (isServer) {
			return game.getPlayerUsernames();
		} else {
			try {
				LinkedList<String> usernames = new LinkedList<String>();
				DataInputStream readFromServer = new DataInputStream(server.getInputStream());
				DataOutputStream writeToServer = new DataOutputStream(server.getOutputStream());
				writeToServer.writeUTF("GETPLAYERUSERNAMES#");
				writeToServer.flush();

				// In case there are no usernames, the server sends an empty string
				do {
					String username = readFromServer.readUTF();
					if (!username.equals("")) {
						usernames.add(username);
					}
				}
				while (readFromServer.available() > 0);

				Log.i(TAG, "Server has " + usernames.size() + " usernames");
				return usernames;
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}

		return null;
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
