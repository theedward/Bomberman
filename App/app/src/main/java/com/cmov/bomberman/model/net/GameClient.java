package com.cmov.bomberman.model.net;

import android.util.Log;
import com.cmov.bomberman.model.Game;
import com.cmov.bomberman.model.Player;
import pt.utl.ist.cmov.wifidirect.sockets.SimWifiP2pSocket;

import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * Only starts receiving updates after it has performed join.
 */
public class GameClient implements Game {
	private static final int GAME_SERVER_PORT = 10001;

	private final String TAG = getClass().getSimpleName();

	private CommunicationChannel commChan;

	private Player localPlayer;

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

	public void pause(final String username) {
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

	public void unpause(final String username) {
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

	public void quit(final String username) {
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

	public void join(final String username, final Player player) {
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

	public void start() {
		// Nothing to do here
	}

	public void onDestroy() {
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
}
