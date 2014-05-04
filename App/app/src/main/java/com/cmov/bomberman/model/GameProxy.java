package com.cmov.bomberman.model;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import java.io.*;
import java.net.Socket;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GameProxy extends Service implements Game {
    private final String TAG = this.getClass().getSimpleName();
    private final IBinder mBinder = new GameBinder();

    private GameImpl game;
    private boolean isMultiplayer;
    private int level;

	//TODO: estas variáveis vão ter de sair, supostamente têm de chegar cá de outra forma
	private ExecutorService executor;
	private TreeMap<Player, Socket> sockets = new TreeMap<Player, Socket>();
	private Socket serverSocket;
    private boolean isServer;

    private void init() {
		game = new GameImpl(level);
		executor = Executors.newSingleThreadExecutor();

        Log.i(TAG, "Created the game");
    }


    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        Bundle extras = intent.getExtras();
        if (extras != null) {
            this.level = extras.getInt("level");
            this.isMultiplayer = extras.getBoolean("isMultiplayer");
            this.isServer = extras.getBoolean("isServer");
        }

        GameUtils.CONTEXT = this;
        init();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    //Gets the array of sockets from the TreeMap
    public Socket[] getSockets() {
        int numSocket = 0;
        Socket[] clientSockets = new Socket[sockets.size()];
        for (Map.Entry<Player, Socket> entry : sockets.entrySet()) {
            clientSockets[numSocket] = entry.getValue();
            numSocket++;
        }
        return clientSockets;
    }

	//Gets the array of players from the TreeMap
    public Player[] getPlayers() {
        int numPlayer = 0;
        Player[] players = new Player[sockets.size()];
        for (Map.Entry<Player, Socket> entry : sockets.entrySet()) {
            players[numPlayer] = entry.getKey();
            numPlayer++;
        }
        return players;
    }

    //Creates DataInputStreams for all the sockets in the sockets TreeMap
    public DataInputStream[] getInputReaders() {
        int socketsSize = sockets.size();
        Socket[] clientSockets = getSockets();
        InputStream[] inputStreams = new InputStream[socketsSize];
        DataInputStream[] dataInputStreams = new DataInputStream[socketsSize];
        try {
            for (int i = 0; i < socketsSize; i++) {
                inputStreams[i] = clientSockets[i].getInputStream();
                dataInputStreams[i] = new DataInputStream(inputStreams[i]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dataInputStreams;
    }

    //Creates DataOutputStreams for all the sockets in the sockets TreeMap
    public DataOutputStream[] getOutputReaders() {
        int socketsSize = sockets.size();
        Socket[] clientSockets = getSockets();
        OutputStream[] outputStreams = new DataOutputStream[socketsSize];
        DataOutputStream[] dataOutputStreams = new DataOutputStream[socketsSize];
        try {
            for (int i = 0; i < socketsSize; i++) {
                outputStreams[i] = clientSockets[i].getOutputStream();
                dataOutputStreams[i] = new DataOutputStream(outputStreams[i]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dataOutputStreams;
    }

    //This function will analyse the message the socket has and call the functions
    // necessairy to perform the given task
    public void analyzeMessage(String message) {
        String[] parts = message.split("#");
        //pause message should be PAUSE#username
        //unpause message should be UNPAUSE#username
        if (parts[0].equals("PAUSE")) {
            pause(parts[1]);
        } else if (parts[0].equals("UNPAUSE")) {
            unpause(parts[1]);
        } else if (parts[0].equals("QUIT")) {
            quit(parts[1]);
        } else if (parts[0].equals("JOIN")) {
            //TODO: need to have the player here, getPlayerByUsername would be nice
        } else if (parts[0].equals("START")) {
            start();
        } else if (parts[0].equals("GETMAPWIDTH")) {
            getMapWidth();
        } else if (parts[0].equals("GETMAPHEIGHT")) {
            getMapHeight();
        }
    }

    //This function should be used in the main while loop, reading from sockets
    public void readFromSockets() {
        DataInputStream[] readers = getInputReaders();
        String message = new String();
        try {
            for (DataInputStream is : readers) {
                if (is.available() != 0)
                    message = is.readUTF();
                analyzeMessage(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

		executor.shutdownNow();
        game = null;
    }

    /* If i'm not the server i only need to send a message to it with the right protocol
        If i am the server i just need to call the function with the right player's username
     */
    @Override
    public void pause(final String username) {
        if (isMultiplayer && !isServer) {
            try {
                DataOutputStream writeToServer = new DataOutputStream(serverSocket.getOutputStream());
                writeToServer.writeChars("PAUSE#" + username);
                writeToServer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            game.pause(username);
        }
    }

    /* If i'm not the server i only need to send a message to it with the right protocol
       If i am the server i just need to call the function with the right player's username
    */
    @Override
    public void unpause(final String username) {

        if (isMultiplayer && !isServer) {
            try {
                DataOutputStream writeToServer = new DataOutputStream(serverSocket.getOutputStream());
                writeToServer.writeChars("UNPAUSE#" + username);
                writeToServer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            game.unpause(username);
        }
    }

    @Override
    public void quit(final String username) {
        if (isMultiplayer && !isServer) {
            try {
                DataOutputStream writeToServer = new DataOutputStream(serverSocket.getOutputStream());
                writeToServer.writeChars("QUIT#" + username);
                writeToServer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            game.quit(username);
        }
    }

    @Override
    public void join(final String username, Player player) {
        // TODO
        if (isMultiplayer) {
            // TODO create network player or something
        } else {
            game.join(username, player);
        }
    }

	@Override
	public Collection<String> getPlayerUsernames() {
		// TODO to implement
		return new LinkedList<String>() {{ add("Andre"); }};
	}

	@Override
    public int getMapWidth() {
        if (isMultiplayer) {
            try {
                DataOutputStream writeToServer = new DataOutputStream(serverSocket.getOutputStream());
                DataInputStream readFromServer = new DataInputStream(serverSocket.getInputStream());
                if (isServer) {
                    writeToServer.writeInt(getMapWidth());
                    return game.getMapWidth();
                } else {
                    writeToServer.writeChars("GETMAPWIDTH#");
                    writeToServer.flush();
                    return readFromServer.readInt();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            return game.getMapWidth();
        }
        return game.getMapWidth();
    }

	@Override
	public int getMapHeight() {
		if (isMultiplayer) {
            try {
                DataOutputStream writeToServer = new DataOutputStream(serverSocket.getOutputStream());
                DataInputStream readFromServer = new DataInputStream(serverSocket.getInputStream());
                if (isServer) {
                    writeToServer.writeInt(getMapHeight());
                    game.getMapWidth();
                } else {
                    writeToServer.writeChars("GETMAPHEIGHT#");
                    writeToServer.flush();
                    return readFromServer.readInt();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
		} else {
			return game.getMapHeight();
		}
        return -1; //same as previous method
	}

	@Override
	public void start() {
		if (isMultiplayer) {
            try {
                DataOutputStream writeToServer = new DataOutputStream(serverSocket.getOutputStream());
                if (isServer) {
                    game.start();
                } else {
                    writeToServer.writeChars("START#");
                    writeToServer.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
		}
	}

	@Override
	public void pause() {
		// TODO
	}

	@Override
	public void unpause() {
		// TODO
	}

	public class GameBinder extends Binder {
		public GameProxy getService() {
			// Return this instance of GameService so clients can call public methods
			return GameProxy.this;
		}
	}
}
