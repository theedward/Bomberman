package com.cmov.bomberman.model;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GameProxy extends Service implements Game {
    private final String TAG = this.getClass().getSimpleName();
    private final IBinder mBinder = new Binder() {
		public GameProxy getService() {
			return GameProxy.this;
		}
	};

    private GameImpl game;
    private int level;

	private ExecutorService executor;
	private Map<String, Socket> clientSockets = new TreeMap<String, Socket>();
	private ServerSocket serverSocket;
	private Socket server;
    private boolean isServer;

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        Bundle extras = intent.getExtras();
        if (extras != null) {
            this.level = extras.getInt("level");
            this.isServer = extras.getBoolean("isServer");
			if (isServer) {
				// read client ports
			} else {
				// read server host & port
			}
        }

        GameUtils.CONTEXT = this;
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

		executor.shutdownNow();
		game = null;
	}

//	//Gets the array of players from the TreeMap
//    public Player[] getPlayers() {
//        int numPlayer = 0;
//        Player[] players = new Player[clientSockets.size()];
//        for (Map.Entry<Player, Socket> entry : clientSockets.entrySet()) {
//            players[numPlayer] = entry.getKey();
//            numPlayer++;
//        }
//        return players;
//    }

    // Creates DataInputStreams for all the sockets in the sockets TreeMap
    public Collection<DataInputStream> getInputStreams() {
		List<DataInputStream> inputStreams = new LinkedList<DataInputStream>();
		try {
			for (Socket s : clientSockets.values()) {
				inputStreams.add(new DataInputStream(s.getInputStream()));
			}
        } catch (IOException e) {
            e.printStackTrace();
        }
		return inputStreams;
    }

    // Creates DataOutputStreams for all the clientSockets in the clientSockets TreeMap
	public Collection<DataOutputStream> getOutputStreams() {
		List<DataOutputStream> inputStreams = new LinkedList<DataOutputStream>();
		try {
			for (Socket s : clientSockets.values()) {
				inputStreams.add(new DataOutputStream(s.getOutputStream()));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return inputStreams;
	}

    //This function will analyse the message the socket has and call the functions
    // necessairy to perform the given task
    public void analyzeMessage(String message) {
        String[] parts = message.split("#");
		String msgType = "";
		String username = "";
		if (parts.length == 2) {
			msgType = parts[0];
			username = parts[1];
		} else {
			Log.e(TAG, "Server received message without 2 arguments.");
		}

        if (msgType.equals("PAUSE")) {
            pause(username);
        } else if (msgType.equals("UNPAUSE")) {
            unpause(username);
        } else if (msgType.equals("QUIT")) {
            quit(username);
        } else if (msgType.equals("JOIN")) {
            join(username, null);
        } else if (msgType.equals("GETMAPWIDTH")) {
            int width = getMapWidth();
			replyWidth(username, width);
        } else if (msgType.equals("GETMAPHEIGHT")) {
            int height = getMapHeight();
			replyHeight(username, height);
        } else if (msgType.equals("GETPLAYERUSERNAMES")) {
			Collection<String> usernames = getPlayerUsernames();
			replyGetPlayerUsernames(username, usernames);
		}
    }

    //This function should be used in the main while loop, reading from clientSockets
    public void readFromSockets() {
        Collection<DataInputStream> inputStreams = getInputStreams();
        String message;
        try {
            for (DataInputStream is : inputStreams) {
                if (is.available() > 0) {
					message = is.readUTF();
					analyzeMessage(message);
				}
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

	private void replyWidth(String username, int width) {
		try {
			DataOutputStream out = new DataOutputStream(clientSockets.get(username).getOutputStream());
			out.writeInt(width);
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void replyHeight(String username, int height) {
		try {
			DataOutputStream out = new DataOutputStream(clientSockets.get(username).getOutputStream());
			out.writeInt(height);
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
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
			// TODO create network player
			Player networkPlayer = null;
            game.join(username, networkPlayer);
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
    public int getMapWidth() {
		try {
			if (isServer) {
				return game.getMapWidth();
			} else {
				DataInputStream readFromServer = new DataInputStream(server.getInputStream());
				DataOutputStream writeToServer = new DataOutputStream(server.getOutputStream());
				writeToServer.writeUTF("GETMAPWIDTH#");
				writeToServer.flush();
				return readFromServer.readInt();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
        return -1;
    }

	@Override
	public int getMapHeight() {
		try {
			if (isServer) {
				return game.getMapWidth();
			} else {
				DataInputStream readFromServer = new DataInputStream(server.getInputStream());
				DataOutputStream writeToServer = new DataOutputStream(server.getOutputStream());
				writeToServer.writeUTF("GETMAPHEIGHT#");
				writeToServer.flush();
				return readFromServer.readInt();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return -1;
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
