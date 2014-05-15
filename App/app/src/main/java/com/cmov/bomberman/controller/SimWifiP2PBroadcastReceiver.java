package com.cmov.bomberman.controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import pt.utl.ist.cmov.wifidirect.SimWifiP2pBroadcast;
import pt.utl.ist.cmov.wifidirect.SimWifiP2pInfo;

public class SimWifiP2pBroadcastReceiver extends BroadcastReceiver {
	private P2pApplication mApplication;

	public SimWifiP2pBroadcastReceiver(final P2pApplication mApplication) {
		super();
		this.mApplication = mApplication;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (SimWifiP2pBroadcast.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {

			// This action is triggered when the WDSim service changes state:
			// - creating the service generates the WIFI_P2P_STATE_ENABLED event
			// - destroying the service generates the WIFI_P2P_STATE_DISABLED event

			int state = intent.getIntExtra(SimWifiP2pBroadcast.EXTRA_WIFI_STATE, -1);
			if (state == SimWifiP2pBroadcast.WIFI_P2P_STATE_ENABLED) {
				mApplication.onWifiOn();
			} else {
				mApplication.onWifiOff();
			}
		} else if (SimWifiP2pBroadcast.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
			// Request available peers from the wifi p2p manager. This is an
			// asynchronous call and the calling activity is notified with a
			// callback on PeerListListener.onPeersAvailable()
			mApplication.onPeersChanged();
		} else if (SimWifiP2pBroadcast.WIFI_P2P_NETWORK_MEMBERSHIP_CHANGED_ACTION.equals(action)) {
			SimWifiP2pInfo ginfo = (SimWifiP2pInfo) intent.getSerializableExtra(
					SimWifiP2pBroadcast.EXTRA_GROUP_INFO);
			ginfo.print();

			mApplication.onNetworkMembershipChanged(ginfo);
		} else if (SimWifiP2pBroadcast.WIFI_P2P_GROUP_OWNERSHIP_CHANGED_ACTION.equals(action)) {
			SimWifiP2pInfo ginfo = (SimWifiP2pInfo) intent.getSerializableExtra(
					SimWifiP2pBroadcast.EXTRA_GROUP_INFO);
			ginfo.print();

			mApplication.onGroupOwnershipChanged(ginfo);
		}
	}
}
