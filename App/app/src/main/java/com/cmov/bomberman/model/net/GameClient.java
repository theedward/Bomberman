package com.cmov.bomberman.model.net;

import android.util.Log;
import com.cmov.bomberman.model.Game;
import com.cmov.bomberman.model.Player;
import pt.utl.ist.cmov.wifidirect.sockets.SimWifiP2pSocket;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Only starts receiving updates after it has performed join.
 */
public class GameClient implements Game {
	private static final int GAME_SERVER_PORT = 10001;

	private final String TAG = getClass().getSimpleName();

	private ExecutorService executor;
	private CommunicationChannel commChan;

	private Player localPlayer;

	/**
	 * Client constructor
	 */
	public GameClient(final String hostname) {
		executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

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
			}
		}

		Log.i(TAG, "Successfully joined the game server");
	}

	public void pause(final String username) {
		executor.execute(new Runnable() {
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
		});
	}

	public void unpause(final String username) {
		executor.execute(new Runnable() {
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
		});
	}

	public void quit(final String username) {
		executor.execute(new Runnable() {
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
		});
	}

	public void join(final String username, final Player player) {
		Log.i(TAG, "Executing join");
		executor.execute(new Runnable() {
			@Override
			public void run() {
				localPlayer = player;

				// handle game requests
				try {
					executor.submit(new PlayerConnectionHandler(localPlayer, commChan));
				}
				catch (IOException e) {
					e.printStackTrace();
				}

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
		});
	}

	public void start() {
		// Nothing to do here
	}

	public void pause() {
		// Nothing to do here
	}

	public void unpause() {
		// Nothing to do here
	}

	public void onDestroy() {
		executor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					commChan.close();
				}
				catch (IOException e) {
					// Stream already closed
				}
			}
		});

		// Shutdown other threads
		executor.shutdownNow();

		Log.i(TAG, "OnDestroy was successful");
	}
}
