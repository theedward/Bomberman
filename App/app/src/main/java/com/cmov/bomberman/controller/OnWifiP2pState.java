package com.cmov.bomberman.controller;

import pt.utl.ist.cmov.wifidirect.SimWifiP2pInfo;

public interface OnWifiP2pState {
	void onWifiOn();
	void onWifiOff();
	void onPeersChanged();
	void onNetworkMembershipChanged(SimWifiP2pInfo info);
	void onGroupOwnershipChanged(SimWifiP2pInfo info);
}
