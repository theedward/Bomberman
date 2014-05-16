package com.cmov.bomberman.model.net;

import android.util.Log;
import com.cmov.bomberman.model.Game;
import com.cmov.bomberman.model.Player;
import com.cmov.bomberman.model.net.dto.GameDto;
import pt.utl.ist.cmov.wifidirect.sockets.SimWifiP2pSocket;

import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * Only starts receiving updates after it has performed join.
 */
public class GameClient implements Game {
	private static final int GAME_SERVER_PORT = 10001;

	private final String TAG = getClass().getSimpleName();

	private Player localPlayer;
	private CommunicationChannel commChan;
	private GameDto gameDto;

	/**
	 * Client constructor
	 */
	public GameClient(final String hostname) {
		// keeps trying to connect to the server until it successfully connects
		while (true) {
			try {
				SimWifiP2pSocket gameSocket = new SimWifiP2pSocket(hostname, GAME_SERVER_PORT);
				this.commChan = new CommunicationChannel(gameSocket);
				break;
			}
			catch (IOException e) {
				// Server is still not available
				Log.e(TAG, "Failed to join the game server");
				try {
					Thread.sleep(1000);
				} catch (InterruptedException ie) {
					return;
				}
			}
		}

		Log.i(TAG, "Successfully joined the game server");
	}

	@Override
	public synchronized void pause(final String username) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					ObjectOutputStream out = commChan.getOut();
					out.writeUTF("pause");
					out.writeUTF(username);
					out.flush();
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	@Override
	public synchronized void unpause(final String username) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					ObjectOutputStream out = commChan.getOut();
					out.writeUTF("unpause");
					out.writeUTF(username);
					out.flush();
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	@Override
	public synchronized void quit(final String username) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					ObjectOutputStream out = commChan.getOut();
					out.writeUTF("quit");
					out.writeUTF(username);
					out.flush();
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();

		onDestroy();
	}

	@Override
	public synchronized void join(final String username, final Player player) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				localPlayer = player;

				// handle game requests
					new Thread(new PlayerConnectionHandler(username, localPlayer, commChan)).start();

				try {
					ObjectOutputStream out = commChan.getOut();
					out.writeUTF("join");
					out.writeUTF(username);
					out.flush();

					Log.i(TAG, "Sent the join message");
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	@Override
	public synchronized void start() {
		// Nothing to do here
	}

	public synchronized void onDestroy() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					commChan.close();
				}
				catch (IOException e) {
					// Stream already closed
				}
			}
		}).start();

		Log.i(TAG, "OnDestroy was successful");
	}

	/**
	 * Called when this client becomes the owner of the game
	 */
	public synchronized GameDto onGameOwner() {
		// Get game state
		try {
			ObjectOutputStream out = commChan.getOut();
			out.writeUTF("getGameState");
			out.flush();
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		// Wait until the server replies with the set game state
		try {
			this.wait();
		} catch (InterruptedException e) {
			return null;
		}

		// Here the server has the dto, return it to the activity
		return this.gameDto;
	}

	public void setGameDto(GameDto dto) {
		this.gameDto = dto;
	}
}
