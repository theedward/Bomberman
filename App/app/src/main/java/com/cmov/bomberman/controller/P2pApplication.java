package com.cmov.bomberman.controller;

import android.app.Application;
import android.content.*;
import android.os.IBinder;
import android.os.Messenger;
import android.widget.Toast;
import pt.utl.ist.cmov.wifidirect.SimWifiP2pBroadcast;
import pt.utl.ist.cmov.wifidirect.SimWifiP2pInfo;
import pt.utl.ist.cmov.wifidirect.SimWifiP2pManager;
import pt.utl.ist.cmov.wifidirect.SimWifiP2pManager.GroupInfoListener;
import pt.utl.ist.cmov.wifidirect.SimWifiP2pManager.PeerListListener;
import pt.utl.ist.cmov.wifidirect.service.SimWifiP2pService;
import pt.utl.ist.cmov.wifidirect.sockets.SimWifiP2pSocketManager;

public class P2pApplication extends Application implements OnWifiP2pState {
	private final String TAG = "P2pApplication";

	private static P2pApplication instance;

	private Messenger mService;
	private SimWifiP2pManager mManager = null;
	private SimWifiP2pManager.Channel mChannel = null;
	private OnWifiP2pState onWifiP2pState;
	private boolean mBound;

	private ServiceConnection mConnection = new ServiceConnection() {
		// callbacks for service binding, passed to bindService()

		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			mService = new Messenger(service);
			mManager = new SimWifiP2pManager(mService);
			mChannel = mManager.initialize(getApplicationContext(), getMainLooper(), null);
			mBound = true;
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			mService = null;
			mManager = null;
			mChannel = null;
			mBound = false;
		}
	};

	public void onCreate() {
		super.onCreate();

		instance = this;

		// initialize the WDSim API
		SimWifiP2pSocketManager.Init(getApplicationContext());

		// register broadcast receiver
		IntentFilter filter = new IntentFilter();
		filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_STATE_CHANGED_ACTION);
		filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_PEERS_CHANGED_ACTION);
		filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_NETWORK_MEMBERSHIP_CHANGED_ACTION);
		filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_GROUP_OWNERSHIP_CHANGED_ACTION);
		SimWifiP2pBroadcastReceiver receiver = new SimWifiP2pBroadcastReceiver(this);
		registerReceiver(receiver, filter);

		// Initialize SimWifiP2pService
		Intent intent = new Intent(getApplicationContext(), SimWifiP2pService.class);
		bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
		mBound = true;
	}

	public static P2pApplication getInstance() {
		return instance;
	}

	public void setOnWifiP2pState(final OnWifiP2pState onWifiP2pState) {
		this.onWifiP2pState = onWifiP2pState;
	}

	public void onWifiOff() {
		if (mBound) {
			unbindService(mConnection);
			mBound = false;
		}

		if (onWifiP2pState != null) {
			onWifiP2pState.onWifiOff();
		}
	}

	public void onWifiOn() {
		if (onWifiP2pState != null) {
			onWifiP2pState.onWifiOn();
		}
	}

	public void onPeersChanged() {
		// TODO
		if (onWifiP2pState != null) {
			onWifiP2pState.onPeersChanged();
		}
	}

	public void onNetworkMembershipChanged(SimWifiP2pInfo info) {
		// TODO
		if (onWifiP2pState != null) {
			onWifiP2pState.onNetworkMembershipChanged(info);
		}
	}

	public void onGroupOwnershipChanged(SimWifiP2pInfo info) {
		// TODO
		if (onWifiP2pState != null) {
			onWifiP2pState.onGroupOwnershipChanged(info);
		}
	}

	public void requestInRange(PeerListListener peerListListener) {
		if (mBound) {
			mManager.requestPeers(mChannel, peerListListener);
		} else {
			Toast.makeText(getApplicationContext(), "Service not bound", Toast.LENGTH_SHORT).show();
		}
	}

	public void requestInGroup(GroupInfoListener groupInfoListener) {
		if (mBound) {
			mManager.requestGroupInfo(mChannel, groupInfoListener);
		} else {
			Toast.makeText(getApplicationContext(), "Service not bound",
						   Toast.LENGTH_SHORT).show();
		}
	}
}
