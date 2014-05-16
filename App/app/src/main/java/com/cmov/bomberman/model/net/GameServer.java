package com.cmov.bomberman.model.net;

import android.util.Log;
import com.cmov.bomberman.model.Game;
import com.cmov.bomberman.model.GameImpl;
import com.cmov.bomberman.model.Player;
import com.cmov.bomberman.model.net.dto.GameDto;
import pt.utl.ist.cmov.wifidirect.sockets.SimWifiP2pSocket;
import pt.utl.ist.cmov.wifidirect.sockets.SimWifiP2pSocketServer;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.SocketException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class GameServer implements Game {
    private static final int GAME_SERVER_PORT = 10001;

    private final String TAG = getClass().getSimpleName();

    private final Map<String, PlayerProxy> playerProxies;
    private GameImpl game;
    private SimWifiP2pSocketServer serverSocket;
    private List<CommunicationChannel> commChannels;

    public GameServer(int level) {
        game = new GameImpl(level);
        playerProxies = new TreeMap<String, PlayerProxy>();
        commChannels = new LinkedList<CommunicationChannel>();

        acceptPlayers();
    }

	public GameServer(GameDto gameDto) {
		game = new GameImpl();
		game.setLevel(gameDto.getLevel());
		game.setPlayersAgent(gameDto.getPlayersAgent());
		game.setPlayerAgentIdx(gameDto.getPlayerAgentIdx());
		game.setGameState(gameDto.getGameState());
		game.setBombermanIds(gameDto.getBombermanIds());
		game.setBombermanPos(gameDto.getBombermanPos());
		game.setBombermanUsed(gameDto.getBombermanUsed());
		game.setGameConfiguration(gameDto.getGameConfiguration());
		game.setStarted(gameDto.isStarted());
		game.setPaused(gameDto.isPaused());
		game.setWallPositions(gameDto.getWallPositions());
		game.setNumRoundsLeft(gameDto.getNumRoundsLeft());

		playerProxies = new TreeMap<String, PlayerProxy>();
		commChannels = new LinkedList<CommunicationChannel>();

		acceptPlayers();
	}

	public GameImpl getGame() {
		return game;
	}

	private void acceptPlayers() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    serverSocket = new SimWifiP2pSocketServer(GAME_SERVER_PORT);
                } catch (IOException e) {
                    Log.e(TAG, "Can't create server socket");
                    return;
                }

                SimWifiP2pSocket clientSocket;
                CommunicationChannel commChan;
                while (true) {
                    try {
                        clientSocket = serverSocket.accept();
                        commChan = new CommunicationChannel(clientSocket);
                    } catch (SocketException e) {
                        Log.e(TAG, "Server socket is closed");
                        return;
                    } catch (IOException e) {
                        Log.e(TAG, "Error creating streams around client socket");
                        continue;
                    }

                    Log.i(TAG, "Found a client");
                    commChannels.add(commChan);
                    new Thread(new GameConnectionHandler(GameServer.this, commChan)).start();
                }
            }
        }).start();
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

        if (playerProxies.containsKey(username)) {
            PlayerProxy proxy = playerProxies.get(username);
            proxy.onDestroy();
            playerProxies.remove(username);
        } else {
			// local player is quitting
            onDestroy();
        }

    }

    public void notifyPlayersOwnerChanged() {
		Log.i(TAG, "notifying players that the group owner has changed");

		for (CommunicationChannel c : commChannels) {
			try {
				ObjectOutputStream out = c.getOut();
				out.writeUTF("groupOwnerChanged");
				out.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
    }

    public void join(final String username, Player player) {
        game.join(username, player);
    }

    public void start() {
        game.start();
    }

    public void setNextActionName(String username, String action) {
        if (playerProxies.containsKey(username)) {
            PlayerProxy proxy = playerProxies.get(username);
            proxy.setNextActionName(action);
        }
    }

    public void addProxy(String username, PlayerProxy proxy) {
        playerProxies.put(username, proxy);
    }

    public void onDestroy() {
        // Stop accepting new requests
        try {
            serverSocket.close();
        } catch (IOException e) {
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

        Log.i(TAG, "OnDestroy was successful");
    }
}
